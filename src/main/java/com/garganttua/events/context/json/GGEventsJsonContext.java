package com.garganttua.events.context.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.objects.GGEventsUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContext implements IGGEventsContextItemBinder<IGGEventsContext> {
	
	@JsonInclude
	private String tenantId;
	@JsonInclude
	private String clusterId; 
	@JsonInclude	
	private List<GGEventsJsonContextTopic> topics;
	@JsonInclude
	private List<GGEventsJsonContextDataflow> dataflows;
	@JsonInclude
	private List<GGEventsJsonContextSubscription> subscriptions;
	@JsonInclude
	private List<GGEventsJsonContextConnector> connectors;
	@JsonInclude
	private List<GGEventsJsonContextRoute> routes;
	@JsonInclude
	private List<GGEventsJsonContextLock> locks;
	
	@Override
	public IGGEventsContext bind() throws GGEventsException {
		IGGEventsContext context = new GGEventsContext(tenantId, clusterId);
		
		this.bindList(this.topics, context.getTopics());
		this.bindList(this.dataflows, context.getDataflows());
		this.bindList(this.subscriptions, context.getSubscriptions());
		this.bindList(this.connectors, context.getConnectors());
		this.bindList(this.routes, context.getRoutes());
		this.bindList(this.locks, context.getLocks());
		
		return context;
	}

	private <contextItem, jsonItem extends IGGEventsContextItemBinder<contextItem>> void bindList(List<jsonItem> listFrom, List<contextItem> listTo) throws GGEventsException {
		for(jsonItem item: listFrom){
			listTo.add(item.bind());
		};
	}
	
	@Override
	public void build(IGGEventsContext bound) throws GGEventsException {
		this.clusterId = bound.getClusterId();
		this.tenantId = bound.getTenantId();
		
		this.buildList(bound.getTopics(), this.topics, GGEventsJsonContextTopic.class);
		this.buildList(bound.getDataflows(), this.dataflows, GGEventsJsonContextDataflow.class);
		this.buildList(bound.getSubscriptions(), this.subscriptions, GGEventsJsonContextSubscription.class);
		this.buildList(bound.getConnectors(), this.connectors, GGEventsJsonContextConnector.class);
		this.buildList(bound.getRoutes(), this.routes, GGEventsJsonContextRoute.class);
		this.buildList(bound.getLocks(), this.locks, GGEventsJsonContextLock.class);
	}

	private <contextItem, jsonItem extends IGGEventsContextItemBinder<contextItem>> void buildList(List<contextItem> listFrom, List<jsonItem> listTo, Class<jsonItem> jsonItemClass) throws GGEventsException {
		for(contextItem contextItem: listFrom) {
			jsonItem jsonItem = null;
			jsonItem = GGEventsUtils.getInstanceOf(jsonItemClass);
			jsonItem.build(contextItem);
			listTo.add(jsonItem);
		};
	}
}
