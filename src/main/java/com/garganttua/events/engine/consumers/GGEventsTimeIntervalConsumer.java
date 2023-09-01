/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.consumers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.context.GGEventsContextTimeInterval;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsConsumer;
import com.garganttua.events.spec.interfaces.IGGEventsRoute;
import com.garganttua.events.spec.interfaces.IGGEventsTypable;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class GGEventsTimeIntervalConsumer implements IGGEventsConsumer, Runnable, IGGEventsTypable {
	
	private ScheduledExecutorService scheduledExecutorService;
	private ExecutorService executorService;
	
	private Object locker = new Object();
	private GGEventsExchange message = null;
	
	private List<IGGEventsRoute> routes;
	private String subscriptionId;
	private IGGEventsConnector connector;
	private GGEventsContextTimeInterval timeInterval;
	
	public GGEventsTimeIntervalConsumer(String subscriptionId, IGGEventsConnector connector, GGEventsContextTimeInterval timeInterval) {
		this.subscriptionId = subscriptionId;
		this.connector = connector;
		this.timeInterval = timeInterval;
	}

	@Override
	public void run() {
		log.info("["+this.subscriptionId+"][ExchangeId:"+this.message.getExchangeId()+"] Message received, dispatching to "+this.routes.size()+" routes");
		log.debug(new String(this.message.getValue()));
		this.routes.forEach(r -> {
			Thread t = new Thread() {
				public void run() {
					try {
						synchronized (locker) {
							if( message != null ) {
								r.handle(message);
							}
						}
					} catch (GGEventsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			};
			this.executorService.execute(t);
		});
	}

	@Override
	public void registerRoute(IGGEventsRoute route) {
		this.routes.add(route);
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsProcessingException, GGEventsException {
		synchronized (this.locker) {
			this.message = exchange;
		}
	}

	@Override
	public String getType() {
		return "consumer::time-interval";
	}
	
}
