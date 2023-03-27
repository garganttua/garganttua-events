package com.gtech.garganttua.core.spec.interfaces;

import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.objects.GGExchange;

public interface IGGEnrichStrategy {
	
	void enrich(String tenantId, GGExchange message, Object dataSource) throws GGCoreProcessingException;

}
