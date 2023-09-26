/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.garganttua.events.spec.enums.GGEventsJourneyStepDirection;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.garganttua.events.spec.objects.GGEventsJourneyStep;
import com.garganttua.events.spec.objects.GGEventsMessage;

import lombok.Getter;

public class GGEventsEncapsulatedProtocolOutProcessor implements IGGEventsProcessor {

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
	private String type;

	public GGEventsEncapsulatedProtocolOutProcessor(String assetId, String clusterId, String toTopic,
			String dataflowVersion, String toDataFlowUuid, String toConnector, String subscriptionId) {
		this.assetId = assetId;
		this.subscriptionId = subscriptionId;
		this.clusterId = clusterId;
		this.toTopic = toTopic;
		this.dataflowVersion = dataflowVersion;
		this.toDataFlowUuid = toDataFlowUuid;
		this.toConnector = toConnector;
		this.type = "processor::out-protocol";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId,
			IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsProcessingException, GGEventsException {
		exchange.setToTopic(this.toTopic);
		exchange.setToConnector(this.toConnector);
		exchange.setToDataflowUuid(this.toDataFlowUuid);
		exchange.setMessageId(UUID.randomUUID().toString());

		exchange.getSteps().add(new GGEventsJourneyStep(new Date(), this.assetId, this.subscriptionId,
				GGEventsJourneyStepDirection.OUT, this.dataflowVersion, UUID.randomUUID().toString(), this.clusterId));

		GGEventsMessage message = exchange.toGGEventsMessage();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		byte[] bytes = null;
		try {
			bytes = mapper.writeValueAsBytes(message);
		} catch (JsonProcessingException e) {
			throw new GGEventsProcessingException(e);
		}
		exchange.setValue(bytes);
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		// TODO Auto-generated method stub

	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "outProtocolProcessor", "1.0",
				this.infos, this.manual);
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExecutorService(ExecutorService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScheduledExecutorService(ScheduledExecutorService service) {
		// TODO Auto-generated method stub
		
	}

}
