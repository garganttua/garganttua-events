/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.util.HashMap;
import java.util.Map;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.context.GGEventsContextProcessor;
import com.garganttua.events.spec.interfaces.IGGEventsContextBuilder;

public class GGEventsContextBuilder implements IGGEventsContextBuilder {
	
	private Map<String, Map<String, GGEventsContext>> contexts = new HashMap<String, Map<String,GGEventsContext>>();

	@Override
	public void addContext(GGEventsContext context) {
		String tenantId = context.getTenantId();
		String clusterId = context.getClusterId();
		
		Map<String, GGEventsContext> clustersTenants = this.contexts.get(tenantId);
		
		if( clustersTenants == null ) {
			clustersTenants = new HashMap<String, GGEventsContext>();
			this.contexts.put(tenantId, clustersTenants);
		} 
			
		GGEventsContext clusterContext = clustersTenants.get(clusterId);
		if( clusterContext == null ) {
			clustersTenants.put(clusterId, context);
		} else {
			//TODO : supprimer les doublons !!!!!!!!!!!!!!!!!!
			clusterContext.getConnectors().addAll(context.getConnectors());
			clusterContext.getDataflows().addAll(context.getDataflows());
			clusterContext.getRoutes().addAll(context.getRoutes());
			clusterContext.getSubscriptions().addAll(context.getSubscriptions());
			clusterContext.getTopics().addAll(context.getTopics());
		}
	}

	@Override
	public Map<String, GGEventsContextProcessor> getProcessors() {
		Map<String, GGEventsContextProcessor> processors = new HashMap<String, GGEventsContextProcessor>();
		this.contexts.forEach( (tenantId, clusters) -> {
			clusters.forEach( (clusterId, context) -> {
				context.getRoutes().forEach( route -> {
					route.getProcessors().forEach( (i, processor) -> {
						processors.put(processor.getUuid(), processor);
					});
				});
			});
		});
		return processors;
	}

	@Override
	public Map<String, Map<String, GGEventsContext>> getContext() {
		return this.contexts;
	}

	@Override
	public Map<String, GGEventsContextProcessor> getProcessors(String tenantId, String clusterId) {
		Map<String, GGEventsContextProcessor> processors = new HashMap<String, GGEventsContextProcessor>();
		this.contexts.get(tenantId).get(clusterId).getRoutes().forEach(route -> {
			route.getProcessors().forEach( (i, processor) -> {
				processors.put(processor.getUuid(), processor);
			});
		});
		return processors;
	}

	@Override
	public void flush() {
		this.contexts = new HashMap<String, Map<String,GGEventsContext>>();
	}

}
