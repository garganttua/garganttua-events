package com.garganttua.events.spec.interfaces;

import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.objects.GGEventsExchange;

public interface IGGEventsEnrichStrategy {
	
	void enrich(String tenantId, GGEventsExchange message, Object dataSource) throws GGEventsProcessingException;

}
