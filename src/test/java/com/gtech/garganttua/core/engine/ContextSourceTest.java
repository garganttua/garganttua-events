/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;

@GGEventsContextSource( name="GGEventsFileContextSource" )
public class ContextSourceTest implements IGGEventsContextSource {

	@Override
	public void init(String assetId, String[] configuration) throws GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<GGEventsContext> getContexts(Date now) throws GGEventsCoreException {
		List<GGEventsContext> ret = new ArrayList<GGEventsContext>();
		ret.add(GGEventsCoreTest.context);
		return ret;
	}

}
