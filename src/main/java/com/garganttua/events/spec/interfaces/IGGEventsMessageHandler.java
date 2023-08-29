/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.objects.GGEventsExchange;

@FunctionalInterface
public interface IGGEventsMessageHandler {
	
	void handle(GGEventsExchange exchange) throws GGEventsProcessingException, GGEventsException;
	
}
