/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;




import java.util.Map;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.context.GGEventsContextProcessor;

public interface IGGEventsContextBuilder {
	
	void addContext(GGEventsContext context);
	
	void flush();
	
	Map<String, Map<String, GGEventsContext>> getContext();

	Map<String, GGEventsContextProcessor> getProcessors();

	Map<String, GGEventsContextProcessor> getProcessors(String tenantId, String clusterId);

}
