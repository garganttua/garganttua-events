/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;

@GGEventsContextSource(type = "ContextSourceTest", version = "1.0")
public class ContextSourceTest implements IGGEventsContextSource {

	public ContextSourceTest(String string) {

	}

	public ContextSourceTest() {

	}

	@Override
	public GGEventsContext readContext(String configuration) throws GGEventsException {
		return new GGEventsContext(configuration, configuration);
	}

	@Override
	public void writeContext(String configuration) throws GGEventsException {
		// TODO Auto-generated method stub

	}

	@Override
	public GGEventsContext readContext() throws GGEventsException {
		return new GGEventsContext("", "");
	}

	@Override
	public void writeContext() throws GGEventsException {
		// TODO Auto-generated method stub

	}

}
