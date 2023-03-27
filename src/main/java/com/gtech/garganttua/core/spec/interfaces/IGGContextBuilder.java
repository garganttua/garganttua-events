/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;




import java.util.Map;

import com.gtech.garganttua.core.context.GGContext;
import com.gtech.garganttua.core.context.GGContextProcessor;

public interface IGGContextBuilder {
	
	void addContext(GGContext context);
	
	void flush();
	
	Map<String, Map<String, GGContext>> getContext();

	Map<String, GGContextProcessor> getProcessors();

	Map<String, GGContextProcessor> getProcessors(String tenantId, String clusterId);

}
