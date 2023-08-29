/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.producers;

import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
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
	public void handle(GGEventsExchange exchange) throws GGEventsException, GGEventsProcessingException {
		log.info("["+this.subscriptionId+"][ExchangeId:"+exchange.getExchangeId()+"] Sending message");
		try {
			this.connector.handle(exchange);
		} catch (GGEventsProcessingException e) {
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
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGEventsException {
		// TODO Auto-generated method stub
		
	}

}
