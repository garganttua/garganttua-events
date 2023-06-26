/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


import java.util.Date;
import java.util.List;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;

public interface IGGEventsContextSource {

	void init(String assetId, String[] configuration) throws GGEventsCoreException;

//	List<GGEventsContext> getContexts() throws GGEventsFrameworkException;

	List<GGEventsContext> getContexts(Date now) throws GGEventsCoreException;
}
