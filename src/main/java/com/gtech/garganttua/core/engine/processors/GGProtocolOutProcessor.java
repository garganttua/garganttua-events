/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine.processors;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gtech.garganttua.core.engine.GGSubscription;
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

public class GGProtocolOutProcessor extends GGAbstractProcessor {

	private String assetId;
	private String subscriptionId;
	private String dataflowVersion;
	private String clusterId;
	private String toTopic;
	private boolean encapsulated;
	
	@Getter
	private String configuration;
	private String infos;
	private String manual;

	public GGProtocolOutProcessor(boolean encapsulated, String assetId, String clusterId, String toTopic, String dataflowVersion, String subscriptionId) {
		this.encapsulated = encapsulated;
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.toTopic = toTopic;
		this.dataflowVersion = dataflowVersion;
		this.subscriptionId = subscriptionId;
		this.type = "IGGProcessor::GGProtocolOutProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		exchange.setToTopic(this.toTopic);
		exchange.setToConnector(GGSubscription.getConnector(this.subscriptionId));
		exchange.setToDataflowUuid(GGSubscription.getSubscription(this.subscriptionId));
		if( this.encapsulated ) {
			
			exchange.getSteps().add(new GGRJourneyStep(new Date(), this.assetId, this.subscriptionId, GGRJourneyStepDirection.OUT, this.dataflowVersion, UUID.randomUUID().toString(), this.clusterId));
			
			GGMessage message = exchange.toGGMessage();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			byte[] bytes = null;
			try {
				bytes = mapper.writeValueAsBytes(message);
			} catch (JsonProcessingException e) {
				throw new GGCoreProcessingException(e);
			}
			exchange.setValue(bytes);
		} else {
			//Nothing to do
			
		}

	}

	@Override
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "outProtocolProcessor", "1.0.0", this.infos, this.manual);
	}

}
