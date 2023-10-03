/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.consumers;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConsumer;
import com.garganttua.events.spec.interfaces.IGGEventsRoute;
import com.garganttua.events.spec.interfaces.IGGEventsTypable;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsOnChangeConsumer implements IGGEventsConsumer, IGGEventsTypable {

	private List<IGGEventsRoute> routes;
	private String subscriptionId;
	
	public GGEventsOnChangeConsumer(String subscriptionId) {
		this.subscriptionId = subscriptionId;
		this.routes = new ArrayList<IGGEventsRoute>();
	}

	@Override
	public void registerRoute(IGGEventsRoute route) {
		this.routes.add(route);
	}

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		log.info("["+this.subscriptionId+"][ExchangeId:"+exchange.getExchangeId()+"] Message received, dispatching to "+this.routes.size()+" routes");
		log.debug(new String(exchange.getValue()));
		this.routes.forEach(r -> {
			try {
				r.handle(exchange);
			} catch (GGEventsHandlingException e) {
				log.warn("["+this.subscriptionId+"][ExchangeId:"+exchange.getExchangeId()+"] Route processing aborded", e);
			}
		});
		return true;
	}

	@Override
	public String getType() {
		return "consumer::on-change";
	}
}
