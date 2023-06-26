/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.garganttua.events.engine.GGEventsSubscription;
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

public class GGEventsProtocolOutProcessor extends GGEventsAbstractProcessor {

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

	public GGEventsProtocolOutProcessor(boolean encapsulated, String assetId, String clusterId, String toTopic, String dataflowVersion, String subscriptionId) {
		this.encapsulated = encapsulated;
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.toTopic = toTopic;
		this.dataflowVersion = dataflowVersion;
		this.subscriptionId = subscriptionId;
		this.type = "IGGEventsProcessor::GGEventsProtocolOutProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		exchange.setToTopic(this.toTopic);
		exchange.setToConnector(GGEventsSubscription.getConnector(this.subscriptionId));
		exchange.setToDataflowUuid(GGEventsSubscription.getSubscription(this.subscriptionId));
		if( this.encapsulated ) {
			
			exchange.getSteps().add(new GGEventsRJourneyStep(new Date(), this.assetId, this.subscriptionId, GGEventsRJourneyStepDirection.OUT, this.dataflowVersion, UUID.randomUUID().toString(), this.clusterId));
			
			GGEventsMessage message = exchange.toGGEventsMessage();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			byte[] bytes = null;
			try {
				bytes = mapper.writeValueAsBytes(message);
			} catch (JsonProcessingException e) {
				throw new GGEventsCoreProcessingException(e);
			}
			exchange.setValue(bytes);
		} else {
			//Nothing to do
			
		}

	}

	@Override
	public void applyConfiguration() throws GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "outProtocolProcessor", "1.0.0", this.infos, this.manual);
	}

}
