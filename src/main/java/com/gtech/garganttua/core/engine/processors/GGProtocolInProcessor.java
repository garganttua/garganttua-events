/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine.processors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtech.garganttua.core.spec.enums.GGRJourneyStepDirection;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;
import com.gtech.garganttua.core.spec.objects.GGMessage;
import com.gtech.garganttua.core.spec.objects.GGRJourneyStep;

import lombok.Getter;

public class GGProtocolInProcessor extends GGAbstractProcessor {

	@Getter
	private String configuration;
	private boolean encapsulated;
	private String assetId;
	private String subscriptionId;
	private String version;
	private String cluster;
	private String tenantId;
	private String infos;
	private String manual;

	public GGProtocolInProcessor(boolean encapsulated, String assetId, String clusterId, String subscriptionId, String dataflowVersion) {
		this.encapsulated = encapsulated;
		this.assetId = assetId;
		this.cluster = clusterId;
		this.subscriptionId = subscriptionId;
		this.version = dataflowVersion;
		this.type = "IGGProcessor::GGProtocolInProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		byte[] body = exchange.getValue();
		
		String bodyString = new String(body, StandardCharsets.UTF_8);
		
		ObjectMapper mapper = new ObjectMapper();
		if( this.encapsulated ) {
			GGMessage message = null;
			try {
				message = mapper.readValue(bodyString, GGMessage.class);
			} catch (IOException e) {
				throw new GGCoreProcessingException(e);
			}
			exchange.setCorrelationId(message.getCorrelationId());
			exchange.setSteps(message.getSteps());
			exchange.setTenantId(message.getTenantId());
			exchange.setValue(message.getValue());
			exchange.setHeaders(message.getHeaders());
			exchange.setException(message.getException());
			exchange.setContentType(message.getContentType());
			exchange.setToUuid(message.getToUuid());
		} else {
			exchange.setTenantId(this.tenantId);
		}
		
		String id = null;
		if( exchange.getSteps().size() == 0 ) {
			id = UUID.randomUUID().toString();
		} else {
			GGRJourneyStep previousStep = exchange.getSteps().get(exchange.getSteps().size()-1);
			id = previousStep.getUuid();
		}
		
		exchange.getSteps().add(new GGRJourneyStep(new Date(), this.assetId, this.subscriptionId, GGRJourneyStepDirection.IN, this.version, id, this.cluster));	
	}

	@Override
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "inProtocolProcessor", "1.0.0", this.infos, this.manual);
	}

}
