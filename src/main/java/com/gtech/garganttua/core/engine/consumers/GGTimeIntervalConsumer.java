/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine.consumers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.gtech.garganttua.core.context.GGContextTimeInterval;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGConsumer;
import com.gtech.garganttua.core.spec.interfaces.IGGRoute;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class GGTimeIntervalConsumer implements IGGConsumer, Runnable {
	
	private ScheduledExecutorService scheduledExecutorService;
	private ExecutorService executorService;
	
	private Object locker = new Object();
	private GGExchange message = null;
	
	private List<IGGRoute> routes;
	private String subscriptionId;
	private IGGConnector connector;
	private GGContextTimeInterval timeInterval;
	
	public GGTimeIntervalConsumer(String subscriptionId, IGGConnector connector, GGContextTimeInterval timeInterval) {
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
					} catch (GGCoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			};
			this.executorService.execute(t);
		});
	}

	@Override
	public void registerRoute(IGGRoute route) {
		this.routes.add(route);
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		synchronized (this.locker) {
			this.message = exchange;
		}
	}

	@Override
	public String getType() {
		return "IGGConsumer::GGTimeIntervalConsumer";
	}
	
}
