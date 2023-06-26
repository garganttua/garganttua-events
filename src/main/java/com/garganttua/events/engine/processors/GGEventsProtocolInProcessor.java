/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.spec.enums.GGEventsRJourneyStepDirection;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsAbstractProcessor;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.garganttua.events.spec.objects.GGEventsMessage;
import com.garganttua.events.spec.objects.GGEventsRJourneyStep;

import lombok.Getter;

public class GGEventsProtocolInProcessor extends GGEventsAbstractProcessor {

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

	public GGEventsProtocolInProcessor(boolean encapsulated, String assetId, String clusterId, String subscriptionId, String dataflowVersion) {
		this.encapsulated = encapsulated;
		this.assetId = assetId;
		this.cluster = clusterId;
		this.subscriptionId = subscriptionId;
		this.version = dataflowVersion;
		this.type = "IGGEventsProcessor::GGEventsProtocolInProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		byte[] body = exchange.getValue();
		
		String bodyString = new String(body, StandardCharsets.UTF_8);
		
		ObjectMapper mapper = new ObjectMapper();
		if( this.encapsulated ) {
			GGEventsMessage message = null;
			try {
				message = mapper.readValue(bodyString, GGEventsMessage.class);
			} catch (IOException e) {
				throw new GGEventsCoreProcessingException(e);
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
			GGEventsRJourneyStep previousStep = exchange.getSteps().get(exchange.getSteps().size()-1);
			id = previousStep.getUuid();
		}
		
		exchange.getSteps().add(new GGEventsRJourneyStep(new Date(), this.assetId, this.subscriptionId, GGEventsRJourneyStepDirection.IN, this.version, id, this.cluster));	
	}

	@Override
	public void applyConfiguration() throws GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "inProtocolProcessor", "1.0.0", this.infos, this.manual);
	}

}
