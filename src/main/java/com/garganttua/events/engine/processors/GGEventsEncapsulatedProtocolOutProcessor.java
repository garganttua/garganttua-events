/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.garganttua.events.spec.enums.GGEventsJourneyStepDirection;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsAbstractProcessor;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.garganttua.events.spec.objects.GGEventsJourneyStep;
import com.garganttua.events.spec.objects.GGEventsMessage;

import lombok.Getter;

public class GGEventsEncapsulatedProtocolOutProcessor extends GGEventsAbstractProcessor {

	private String assetId;
	private String subscriptionId;
	private String dataflowVersion;
	private String clusterId;
	private String toTopic;

	@Getter
	private String configuration;
	private String infos;
	private String manual;
	private String toDataFlowUuid;
	private String toConnector;

	public GGEventsEncapsulatedProtocolOutProcessor(String assetId, String clusterId, String toTopic,
			String dataflowVersion, String toDataFlowUuid, String toConnector) {
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.toTopic = toTopic;
		this.dataflowVersion = dataflowVersion;
		this.toDataFlowUuid = toDataFlowUuid;
		this.toConnector = toConnector;
		this.type = "IGGEventsProcessor::GGEventsProtocolOutProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId,
			IGGEventsObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		exchange.setToTopic(this.toTopic);
		exchange.setToConnector(this.toConnector);
		exchange.setToDataflowUuid(this.toDataFlowUuid);

		exchange.getSteps().add(new GGEventsJourneyStep(new Date(), this.assetId, this.subscriptionId,
				GGEventsJourneyStepDirection.OUT, this.dataflowVersion, UUID.randomUUID().toString(), this.clusterId));

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
	}

	@Override
	public void applyConfiguration() throws GGEventsCoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "outProtocolProcessor", "1.0.0",
				this.infos, this.manual);
	}

}
