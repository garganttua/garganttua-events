/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.exceptions.GGEventsException;

public interface IGGEventsContextSource {
	
	String getSourceName(); 

	GGEventsContext readContext(String configuration) throws GGEventsException;
	
	void writeContext(String configuration) throws GGEventsException;
}
