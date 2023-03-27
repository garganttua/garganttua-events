/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;


public interface IGGConsumer extends IGGMessageHandler {
	
	void registerRoute(IGGRoute ggRoute);
		
}
