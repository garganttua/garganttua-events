/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;

public interface IGGEventsContextSource {
	
	IGGEventsContext readContext(String configuration) throws GGEventsException;
	
	void writeContext(String configuration) throws GGEventsException;

	IGGEventsContext readContext() throws GGEventsException;
	
	void writeContext() throws GGEventsException;
	
}
