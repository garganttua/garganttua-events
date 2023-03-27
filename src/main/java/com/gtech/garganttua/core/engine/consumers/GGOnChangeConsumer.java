/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine.consumers;

import java.util.ArrayList;
import java.util.List;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGConsumer;
import com.gtech.garganttua.core.spec.interfaces.IGGRoute;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGOnChangeConsumer implements IGGConsumer {

	private List<IGGRoute> routes;
	private String subscriptionId;
	
	public GGOnChangeConsumer(String subscriptionId) {
		this.subscriptionId = subscriptionId;
		this.routes = new ArrayList<IGGRoute>();
	}

	@Override
	public void registerRoute(IGGRoute route) {
		this.routes.add(route);
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		log.info("["+this.subscriptionId+"][ExchangeId:"+exchange.getExchangeId()+"] Message received, dispatching to "+this.routes.size()+" routes");
		log.debug(new String(exchange.getValue()));
		this.routes.forEach(r -> {
			try {
				r.handle(exchange);
			} catch (GGCoreException e) {
				log.warn("["+this.subscriptionId+"][ExchangeId:"+exchange.getExchangeId()+"] Route processing aborded", e);
			}
		});
	}

	@Override
	public String getType() {
		return "IGGConsumer::GGOnChangeConsumer";
	}
}
