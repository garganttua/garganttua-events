/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;


import java.util.Date;
import java.util.List;

import com.gtech.garganttua.core.context.GGContext;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;

public interface IGGContextSource {

	void init(String assetId, String[] configuration) throws GGCoreException;

//	List<GGContext> getContexts() throws GGFrameworkException;

	List<GGContext> getContexts(Date now) throws GGCoreException;
}
