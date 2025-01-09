/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConfigurable;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTypable;

public interface IGGEventsContextSource extends IGGEventsContextTypable, IGGEventsContextConfigurable {	
	
	void setConfiguration(String configuration);
	
	IGGEventsContext readContext() throws GGEventsException;
	IGGEventsContext readContext(String configuration) throws GGEventsException;
	IGGEventsContext readContext(String configuration, boolean ignoreSources) throws GGEventsException;
		
	void writeContext(IGGEventsContext context) throws GGEventsException;
	void writeContext(IGGEventsContext context, String configuration) throws GGEventsException;
	void writeContext(IGGEventsContext context, String configuration, boolean ignoreVersion) throws GGEventsException;


	
}
