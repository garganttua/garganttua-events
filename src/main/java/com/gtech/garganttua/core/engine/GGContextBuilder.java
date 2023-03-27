/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.HashMap;
import java.util.Map;

import com.gtech.garganttua.core.context.GGContext;
import com.gtech.garganttua.core.context.GGContextProcessor;
import com.gtech.garganttua.core.spec.interfaces.IGGContextBuilder;

public class GGContextBuilder implements IGGContextBuilder {
	
	private Map<String, Map<String, GGContext>> contexts = new HashMap<String, Map<String,GGContext>>();

	@Override
	public void addContext(GGContext context) {
		String tenantId = context.getTenantId();
		String clusterId = context.getClusterId();
		
		Map<String, GGContext> clustersTenants = this.contexts.get(tenantId);
		
		if( clustersTenants == null ) {
			clustersTenants = new HashMap<String, GGContext>();
			this.contexts.put(tenantId, clustersTenants);
		} 
			
		GGContext clusterContext = clustersTenants.get(clusterId);
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
	public Map<String, GGContextProcessor> getProcessors() {
		Map<String, GGContextProcessor> processors = new HashMap<String, GGContextProcessor>();
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
	public Map<String, Map<String, GGContext>> getContext() {
		return this.contexts;
	}

	@Override
	public Map<String, GGContextProcessor> getProcessors(String tenantId, String clusterId) {
		Map<String, GGContextProcessor> processors = new HashMap<String, GGContextProcessor>();
		this.contexts.get(tenantId).get(clusterId).getRoutes().forEach(route -> {
			route.getProcessors().forEach( (i, processor) -> {
				processors.put(processor.getUuid(), processor);
			});
		});
		return processors;
	}

	@Override
	public void flush() {
		this.contexts = new HashMap<String, Map<String,GGContext>>();
	}

}
