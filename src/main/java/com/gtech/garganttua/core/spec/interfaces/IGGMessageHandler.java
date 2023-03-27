/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.objects.GGExchange;

public interface IGGMessageHandler {
	
	void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException;
	
	String getType();
	
}
