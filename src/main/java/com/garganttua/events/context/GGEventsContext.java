/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.garganttua.events.engine.GGEventsBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConnector;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextLock;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextMergeableItem;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextRoute;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTimeInterval;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTopic;
import com.garganttua.events.spec.interfaces.context.IGGEventsSourcedContextItem;

import lombok.Getter;

@Getter
public class GGEventsContext extends GGEventsContextItem<GGEventsContext> implements IGGEventsContext {
	
	private String tenantId;
	
	private String clusterId; 
		
	private List<IGGEventsContextTopic> topics = new ArrayList<IGGEventsContextTopic>();
	
	private List<IGGEventsContextDataflow> dataflows = new ArrayList<IGGEventsContextDataflow>();
	
	private List<IGGEventsContextSubscription> subscriptions = new ArrayList<IGGEventsContextSubscription>();

	private List<IGGEventsContextConnector> connectors = new ArrayList<IGGEventsContextConnector>();

	private List<IGGEventsContextRoute> routes = new ArrayList<IGGEventsContextRoute>();

	private List<IGGEventsContextLock> locks = new ArrayList<IGGEventsContextLock>();

	private IGGEventsBuilder builder;

	public GGEventsContext(String tenantId, String clusterId) {
		this.clusterId = clusterId;
		this.tenantId = tenantId;
	}
	
	@Override
	public IGGEventsEngine build() {
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T createOrMerge(List<T> items, T item) {
		if( !items.contains(item) ) {
			items.add(item);
			return item;
		} else {
			T toupdate = items.get(items.indexOf(item));
			((IGGEventsContextMergeableItem<T>) toupdate).merge(item);
			return toupdate;
		}
	}
	
	private <T> T setSourceIfNeeded(T item) {
		if( ((IGGEventsSourcedContextItem) item).getsources().isEmpty() ) {
			((IGGEventsSourcedContextItem) item).getsources().add(new GGEventsContextItemSource(this.tenantId, this.clusterId, "built-in", new Date()));
		}
		return item;
	}
	
	@Override
	public IGGEventsContext topic(String ref) {
		return this.topic(new GGEventsContextTopic(ref));
	}
	
	@Override
	public IGGEventsContext dataflow(String uuid, String name, String type, boolean garanteeOrder, String version, boolean encapsulated) {
		return this.dataflow(new GGEventsContextDataflow(uuid, name, type, garanteeOrder, version, encapsulated));
	}

	@Override
	public IGGEventsContextSubscription subscription(String uuid, String topicRef, String connectorName, GGEventsContextPublicationMode publicationMode) {
		GGEventsContextSubscription subscription = new GGEventsContextSubscription(uuid, topicRef, connectorName, publicationMode);
		subscription.context(this);
		return this.subscription(subscription);
	}

	@Override
	public IGGEventsContext connector(String name, String type, String version, String configuration) {
		return this.connector(new GGEventsContextConnector(name, type, configuration, version));
	}

	@Override
	public IGGEventsContextRoute route(String uuid, String from, String to) {
		GGEventsContextRoute route = new GGEventsContextRoute(uuid, from, to);
		route.context(this);
		return this.route(route);
	}

	@Override
	public IGGEventsContext lock(String name, String type, String version, String configuration) {
		return this.lock(new GGEventsContextLock(name, type, version, configuration));
	}
	
	@Override
	public IGGEventsContext topic(IGGEventsContextTopic topic) {
		this.setSourceIfNeeded(topic);
		this.createOrMerge(this.topics, topic);
		return this;
	}

	@Override
	public IGGEventsContext dataflow(IGGEventsContextDataflow dataflow) {
		this.setSourceIfNeeded(dataflow);
		this.createOrMerge(this.dataflows, dataflow);
		return this;
	}

	@Override
	public IGGEventsContextSubscription subscription(IGGEventsContextSubscription subscription) {
		this.setSourceIfNeeded(subscription);
		return this.createOrMerge(this.subscriptions, subscription);
	}

	@Override
	public IGGEventsContext connector(IGGEventsContextConnector connector) {
		this.setSourceIfNeeded(connector);
		this.createOrMerge(this.connectors, connector);
		return this;
	}

	@Override
	public IGGEventsContextRoute route(IGGEventsContextRoute route) {
		this.setSourceIfNeeded(route);
		return this.createOrMerge(this.routes, route);
	}

	@Override
	public IGGEventsContext lock(IGGEventsContextLock lock) {
		this.setSourceIfNeeded(lock);
		this.createOrMerge(this.locks, lock);
		return this;
	}
	
	@Override
	public GGEventsContext merge(GGEventsContext item) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	protected boolean isEqualTo(GGEventsContext item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IGGEventsBuilder builder() {
		return this.builder;
	}

	@Override
	public IGGEventsContext write(String sourceType, String version, String sourceConfiguration) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public IGGEventsContext write(IGGEventsContextSource source) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public IGGEventsContext write() {
		// TODO Auto-generated method stub
		return this;
	}

	public void builder(GGEventsBuilder builder) {
		this.builder = builder;
	}

}
