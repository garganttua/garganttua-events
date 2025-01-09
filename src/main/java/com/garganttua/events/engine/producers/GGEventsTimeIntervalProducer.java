/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.producers;

import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsProducer;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTimeInterval;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsTimeIntervalProducer implements IGGEventsProducer, Runnable {

	private Object blocker = new Object();
	private GGEventsExchange message;
	private IGGEventsConnector connector;
	private String subscriptionId;
	private IGGEventsContextTimeInterval timeInterval;

	public GGEventsTimeIntervalProducer(String subscriptionId, IGGEventsConnector connector,
			IGGEventsContextTimeInterval timeInterval) {
		this.subscriptionId = subscriptionId;
		this.connector = connector;
		this.timeInterval = timeInterval;
	}

	public void stop() {

	}

	public void run() {
		synchronized (this.blocker) {
			if (this.message != null) {
				try {
					log.info("[" + this.subscriptionId + "][ExchangeId:" + this.message.getExchangeId()
							+ "] Sending message");
					this.connector.handle(this.message);
				} catch (GGEventsHandlingException e) {
					e.printStackTrace();
				}
			} else {
				log.info("[" + this.subscriptionId + "][ExchangeId: null] Nothing to send");
			}
		}
	}

	@Override
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGEventsException {
		scheduledExecutorService.scheduleAtFixedRate(this, 0L, this.timeInterval.getInterval(),
				this.timeInterval.getTimeUnit());
	}

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		synchronized (this.blocker) {
			this.message = exchange;
		}
		return true;
	}

	@Override
	public String getType() {
		return "producer::time-interval";
	}

}
