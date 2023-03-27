/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine.producers;

import java.util.concurrent.ScheduledExecutorService;

import com.gtech.garganttua.core.context.GGContextTimeInterval;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGProducer;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGTimeIntervalProducer implements IGGProducer, Runnable {

	private Object blocker = new Object();
	private GGExchange message;
	private IGGConnector connector;
	private String subscriptionId;
	private GGContextTimeInterval timeInterval;

	public GGTimeIntervalProducer(String subscriptionId, IGGConnector connector,
			GGContextTimeInterval timeInterval) {
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
//					log.debug(new String(this.message.getValue()));
					this.connector.handle(this.message);
				} catch (GGCoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				log.info("[" + this.subscriptionId + "][ExchangeId: null] Nothing to send");
			}
		}
	}

	@Override
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGCoreException {
		scheduledExecutorService.scheduleAtFixedRate(this, 0L, this.timeInterval.getInterval(),
				this.timeInterval.getTimeUnit());
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreException {
		synchronized (this.blocker) {
			this.message = exchange;
		}
	}

	@Override
	public String getType() {
		return "IGGProducer::GGTimeIntervalProducer";
	}

}
