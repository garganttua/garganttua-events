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
import com.gtech.garganttua.core.context.GGContextSubscription;
import com.gtech.garganttua.core.spec.annotations.GGConnector;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;
import com.gtech.garganttua.core.spec.objects.GGMessage;
import com.gtech.garganttua.core.spec.objects.GGRJourneyStep;

@GGConnector(type="TestConnector" , version ="1.0")
public class ConnectorTest implements IGGConnector {

	private IGGMessageHandler consumer;


	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void receivedMessage(byte[] bytes) throws JsonProcessingException, GGCoreException, GGCoreProcessingException {
		GGMessage message = new GGMessage(new HashMap<String, String>(), "1", "1", ((List) new ArrayList<GGRJourneyStep>()), "", bytes, MediaType.APPLICATION_JSON_TYPE.toString(), null, null);
		
		ObjectMapper mapper = new ObjectMapper();

		byte[] tata = mapper.writeValueAsBytes(message);
		
		GGExchange toto = GGExchange.emptyExchange("", "", "", tata);
		
		this.consumer.handle(toto);
	}


	@Override
	public void setPoolExecutor(ExecutorService poolExecutor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void applyConfiguration() throws GGCoreException {
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
	public void registerConsumer(GGContextSubscription subscription, IGGMessageHandler messageHandler, String tenantId,
			String clusterId, String assetId) {
		this.consumer = messageHandler;
		
	}

	@Override
	public void registerProducer(GGContextSubscription subscription, String tenantId, String clusterId,
			String assetId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}



}
