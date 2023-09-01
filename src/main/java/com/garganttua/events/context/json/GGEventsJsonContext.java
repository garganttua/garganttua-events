package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.objects.context.GGEventsContextItemBinderUtils;

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
		IGGEventsContext context = new GGEventsContext(this.tenantId, this.clusterId);
		
		GGEventsContextItemBinderUtils.bindList(this.topics, context.getTopics());
		GGEventsContextItemBinderUtils.bindList(this.dataflows, context.getDataflows());
		GGEventsContextItemBinderUtils.bindList(this.subscriptions, context.getSubscriptions());
		GGEventsContextItemBinderUtils.bindList(this.connectors, context.getConnectors());
		GGEventsContextItemBinderUtils.bindList(this.routes, context.getRoutes());
		if( this.locks != null )
			GGEventsContextItemBinderUtils.bindList(this.locks, context.getLocks());
		
		return context;
	}

	@Override
	public void build(IGGEventsContext bound) throws GGEventsException {
		this.clusterId = bound.getClusterId();
		this.tenantId = bound.getTenantId();
		
		this.topics = new ArrayList<GGEventsJsonContextTopic>();
		GGEventsContextItemBinderUtils.buildList(bound.getTopics(), this.topics, GGEventsJsonContextTopic.class);
		this.dataflows = new ArrayList<GGEventsJsonContextDataflow>();
		GGEventsContextItemBinderUtils.buildList(bound.getDataflows(), this.dataflows, GGEventsJsonContextDataflow.class);
		this.subscriptions = new ArrayList<GGEventsJsonContextSubscription>();
		GGEventsContextItemBinderUtils.buildList(bound.getSubscriptions(), this.subscriptions, GGEventsJsonContextSubscription.class);
		this.connectors = new ArrayList<GGEventsJsonContextConnector>();
		GGEventsContextItemBinderUtils.buildList(bound.getConnectors(), this.connectors, GGEventsJsonContextConnector.class);
		this.routes = new ArrayList<GGEventsJsonContextRoute>();
		GGEventsContextItemBinderUtils.buildList(bound.getRoutes(), this.routes, GGEventsJsonContextRoute.class);
		if( bound.getLocks() != null ) {
			this.locks = new ArrayList<GGEventsJsonContextLock>();
			GGEventsContextItemBinderUtils.buildList(bound.getLocks(), this.locks, GGEventsJsonContextLock.class);
		}
	}
}
