/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.spec.enums.GGEventsJourneyStepDirection;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.garganttua.events.spec.objects.GGEventsJourneyStep;
import com.garganttua.events.spec.objects.GGEventsMessage;

import lombok.Getter;

public class GGEventsEncapsulatedProtocolInProcessor implements IGGEventsProcessor {

	@Getter
	private String configuration;
	private String assetId;
	private String subscriptionId;
	private String version;
	private String cluster;
	private String infos;
	private String manual;
	private String type;
	private String name;

	public GGEventsEncapsulatedProtocolInProcessor(String assetId, String clusterId, String subscriptionId,
			String dataflowVersion) {
		this.assetId = assetId;
		this.cluster = clusterId;
		this.subscriptionId = subscriptionId;
		this.version = dataflowVersion;
		this.type = "processor::in-protocol";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId,
			IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine) {
		this.configuration = configuration;
	}

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		byte[] body = exchange.getValue();

		String bodyString = new String(body, StandardCharsets.UTF_8);

		ObjectMapper mapper = new ObjectMapper();
		GGEventsMessage message = null;
		try {
			message = mapper.readValue(bodyString, GGEventsMessage.class);
		} catch (IOException e) {
			throw new GGEventsHandlingException(e);
		}
		exchange.setCorrelationId(message.getCorrelationId());
		exchange.setDataflowVersion(message.getDataflowVersion());
		exchange.setSteps(message.getSteps());
		exchange.setTenantId(message.getTenantId());
		exchange.setValue(message.getValue());
		exchange.setHeaders(message.getHeaders());
		exchange.setException(message.getException());
		exchange.setContentType(message.getContentType());
		exchange.setToUuid(message.getToUuid());
		
		String id = null;
		if (exchange.getSteps().size() == 0) {
			id = UUID.randomUUID().toString();
		} else {
			GGEventsJourneyStep previousStep = exchange.getSteps().get(exchange.getSteps().size() - 1);
			id = previousStep.getUuid();
		}

		exchange.getSteps().add(new GGEventsJourneyStep(new Date(), this.assetId, this.subscriptionId,
				GGEventsJourneyStepDirection.IN, this.version, id, this.cluster));
		
		return true;
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		// TODO Auto-generated method stub

	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "inProtocolProcessor", "1.0",
				this.infos, this.manual);
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setName(String name) {
		this.name = name;	
	}

	@Override
	public String getName() {
		return this.name;
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
