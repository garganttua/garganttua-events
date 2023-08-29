/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.enums.GGEventsMediaType;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.garganttua.events.spec.objects.GGEventsJourneyStep;
import com.garganttua.events.spec.objects.GGEventsMessage;

@GGEventsConnector(type="TestConnector" , version ="1.0")
public class ConnectorTest implements IGGEventsConnector {

	private IGGEventsMessageHandler consumer;


	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void receivedMessage(byte[] bytes) throws JsonProcessingException, GGEventsException, GGEventsProcessingException {
		GGEventsMessage message = new GGEventsMessage(new HashMap<String, String>(), "1", "1", ((List) new ArrayList<GGEventsJourneyStep>()), "", bytes, GGEventsMediaType.APPLICATION_JSON.toString(), null, null, "1.0");
		
		ObjectMapper mapper = new ObjectMapper();

		byte[] tata = mapper.writeValueAsBytes(message);
		
		GGEventsExchange toto = GGEventsExchange.emptyExchange("", "", "", tata);
		
		this.consumer.handle(toto);
	}


	@Override
	public void setPoolExecutor(ExecutorService poolExecutor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsProcessingException, GGEventsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void applyConfiguration() throws GGEventsException {
		// TODO Auto-generated method stub
		
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
	public void registerConsumer(IGGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler, String tenantId,
			String clusterId, String assetId) {
		this.consumer = messageHandler;
		
	}

	@Override
	public void registerProducer(IGGEventsContextSubscription subscription, String tenantId, String clusterId,
			String assetId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}



}
