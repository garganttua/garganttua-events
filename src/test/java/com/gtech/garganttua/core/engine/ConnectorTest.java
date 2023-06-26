/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.garganttua.events.spec.objects.GGEventsMessage;
import com.garganttua.events.spec.objects.GGEventsRJourneyStep;

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

	public void receivedMessage(byte[] bytes) throws JsonProcessingException, GGEventsCoreException, GGEventsCoreProcessingException {
		GGEventsMessage message = new GGEventsMessage(new HashMap<String, String>(), "1", "1", ((List) new ArrayList<GGEventsRJourneyStep>()), "", bytes, MediaType.APPLICATION_JSON_TYPE.toString(), null, null);
		
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
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
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
	public void applyConfiguration() throws GGEventsCoreException {
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
	public void registerConsumer(GGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler, String tenantId,
			String clusterId, String assetId) {
		this.consumer = messageHandler;
		
	}

	@Override
	public void registerProducer(GGEventsContextSubscription subscription, String tenantId, String clusterId,
			String assetId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}



}
