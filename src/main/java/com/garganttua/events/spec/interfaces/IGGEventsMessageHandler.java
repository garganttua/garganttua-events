/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.objects.GGEventsExchange;

@FunctionalInterface
public interface IGGEventsMessageHandler {
	
	boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException;
	
}
