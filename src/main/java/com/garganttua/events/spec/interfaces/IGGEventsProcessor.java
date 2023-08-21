/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

public interface IGGEventsProcessor extends IGGEventsMessageHandler, IGGEventsConfigurable, IGGEventsDescribable {
	
	void setType(String type);

	void setContextEngine(IGGEventsContextEngine framework);

}
