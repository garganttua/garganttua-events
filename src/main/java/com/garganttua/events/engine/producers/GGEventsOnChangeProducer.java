/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.producers;

import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
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
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		log.info("["+this.subscriptionId+"][ExchangeId:"+exchange.getExchangeId()+"] Sending message");
		try {
			this.connector.handle(exchange);
		} catch (GGEventsHandlingException e) {
			throw e;
		}
		return true;
	}


	@Override
	public String getType() {
		return "producer::on-change";
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
