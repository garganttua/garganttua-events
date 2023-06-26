/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


public interface IGGEventsConsumer extends IGGEventsMessageHandler {
	
	void registerRoute(IGGEventsRoute ggRoute);
		
}
