/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gtech.garganttua.core.context.GGContext;
import com.gtech.garganttua.core.spec.annotations.GGContextSource;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGContextSource;

@GGContextSource( name="GGFileContextSource" )
public class ContextSourceTest implements IGGContextSource {

	@Override
	public void init(String assetId, String[] configuration) throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<GGContext> getContexts(Date now) throws GGCoreException {
		List<GGContext> ret = new ArrayList<GGContext>();
		ret.add(GGCoreTest.context);
		return ret;
	}

}
