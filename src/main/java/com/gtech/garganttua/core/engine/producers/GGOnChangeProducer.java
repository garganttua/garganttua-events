/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine.producers;

import java.util.concurrent.ScheduledExecutorService;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGProducer;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGOnChangeProducer implements IGGProducer {

	private IGGConnector connector;
	private String subscriptionId;

	public GGOnChangeProducer(String subscriptionId, IGGConnector connector) {
		this.subscriptionId = subscriptionId;
		this.connector = connector;
	}


	@Override
	public void handle(GGExchange exchange) throws GGCoreException, GGCoreProcessingException {
		log.info("["+this.subscriptionId+"][ExchangeId:"+exchange.getExchangeId()+"] Sending message");
//		log.debug(new String(exchange.getValue()));
		try {
			this.connector.handle(exchange);
		} catch (GGCoreProcessingException e) {
			throw e;
		}
	}


	@Override
	public String getType() {
		return "IGGProducer::GGOnChangeProducer";
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

}
