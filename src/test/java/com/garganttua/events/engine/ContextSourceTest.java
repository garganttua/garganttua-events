/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;

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
	public void writeContext(IGGEventsContext context, String configuration) throws GGEventsException {
		// TODO Auto-generated method stub

	}

	@Override
	public GGEventsContext readContext() throws GGEventsException {
		return new GGEventsContext("", "");
	}

	@Override
	public void writeContext(IGGEventsContext context) throws GGEventsException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
