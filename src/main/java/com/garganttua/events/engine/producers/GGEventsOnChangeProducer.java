/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.producers;

import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsProducer;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsOnChangeProducer implements IGGEventsProducer {

	private IGGEventsConnector connector;
	private String subscriptionId;

	public GGEventsOnChangeProducer(String subscriptionId, IGGEventsConnector connector) {
		this.subscriptionId = subscriptionId;
		this.connector = connector;
	}


	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreException, GGEventsCoreProcessingException {
		log.info("["+this.subscriptionId+"][ExchangeId:"+exchange.getExchangeId()+"] Sending message");
//		log.debug(new String(exchange.getValue()));
		try {
			this.connector.handle(exchange);
		} catch (GGEventsCoreProcessingException e) {
			throw e;
		}
	}


	@Override
	public String getType() {
		return "IGGEventsProducer::GGEventsOnChangeProducer";
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

}
