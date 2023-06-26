/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.objects.GGEventsExchange;

public interface IGGEventsMessageHandler {
	
	void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException;
	
	String getType();
	
}
