/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.garganttua.events.engine.GGEventsBuilder;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConnector;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextLock;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextMergeableItem;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextRoute;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSourcedItem;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTopic;
import com.garganttua.events.spec.objects.GGEventsUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GGEventsContext implements IGGEventsContext, IGGEventsContextMergeableItem<IGGEventsContext> {
	
	@Setter
	private String assetId;
	
	private String tenantId;
	
	private String clusterId; 
		
	private List<IGGEventsContextTopic> topics = new ArrayList<IGGEventsContextTopic>();
	
	private List<IGGEventsContextDataflow> dataflows = new ArrayList<IGGEventsContextDataflow>();
	
	private List<IGGEventsContextSubscription> subscriptions = new ArrayList<IGGEventsContextSubscription>();

	private List<IGGEventsContextConnector> connectors = new ArrayList<IGGEventsContextConnector>();

	private List<IGGEventsContextRoute> routes = new ArrayList<IGGEventsContextRoute>();

	private List<IGGEventsContextLock> locks = new ArrayList<IGGEventsContextLock>();

	private IGGEventsBuilder builder;

	private Map<String, Map<String, Class<?>>> sourcesClasses;

	private List<com.garganttua.events.spec.interfaces.context.IGGEventsContextSource> sources = new ArrayList<com.garganttua.events.spec.interfaces.context.IGGEventsContextSource>();

	public GGEventsContext(String assetId, String tenantId, String clusterId) {
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.tenantId = tenantId;
	}
	
	public GGEventsContext(String tenantId, String clusterId) {
		this(null, tenantId, clusterId);
	}

	@Override
	public IGGEventsEngine build() {
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T extends IGGEventsContextMergeableItem<T>> T createOrMerge(List<T> items, T item) {
		if( !items.contains(item) ) {
			items.add(item);
			return item;
		} else {
			T toupdate = items.get(items.indexOf(item));
			toupdate.merge(item);
			return toupdate;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T setSourceIfNeeded(T item) {
		if( ((IGGEventsContextSourcedItem) item).getsources().isEmpty() ) {
			((IGGEventsContextSourcedItem) item).getsources().add(new GGEventsContextSource(assetId, this.clusterId, "built-in"));
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
		return this.connector(new GGEventsContextConnector(name, type, version, configuration));
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
	public void merge(IGGEventsContext item) {
		((GGEventsContext) item).topics.forEach(topic -> {
			topic(topic);
		});
		((GGEventsContext) item).dataflows.forEach(dataflow ->{
			dataflow(dataflow);
		});
		((GGEventsContext) item).connectors.forEach(connector -> {
			connector(connector);
		});
		((GGEventsContext) item).subscriptions.forEach(subscription -> {
			subscription(subscription);
		});
		((GGEventsContext) item).locks.forEach(lock -> {
			lock(lock);
		});
		((GGEventsContext) item).routes.forEach(route -> {
			route(route);
		});

//		return this;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = true;
		
		GGEventsContext item = (GGEventsContext) obj;
		
		equal &= item.getClusterId().equals(this.clusterId) && item.getTenantId().equals(this.tenantId);
		equal &= this.topics.equals(item.getTopics());
		equal &= this.subscriptions.equals(item.getSubscriptions());
		equal &= this.dataflows.equals(item.getDataflows());
		equal &= this.connectors.equals(item.getConnectors());
		equal &= this.routes.equals(item.getRoutes());
		equal &= this.locks.equals(item.getLocks());

		return equal;
	}

	@Override
	public IGGEventsBuilder builder() {
		return this.builder;
	}

	@Override
	public IGGEventsContext write(String sourceType, String version, String sourceConfiguration) {
		try {
			IGGEventsContextSource source = GGEventsUtils.getSourceObj(sourceType, version, this.sourcesClasses);
			source.writeContext(this, sourceConfiguration);
		} catch (GGEventsException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	@Override
	public IGGEventsContext write(IGGEventsContextSource source) {
		try {
			source.writeContext(this);
		} catch (GGEventsException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public void builder(GGEventsBuilder builder) {
		this.builder = builder;
	}

	public void setSourcesObjects(Map<String, Map<String, Class<?>>> sources) {
		this.sourcesClasses = sources;
	}

	@Override
	public IGGEventsContext source(com.garganttua.events.spec.interfaces.context.IGGEventsContextSource source) {
		if( !this.sources.contains(source) )
			this.sources.add(source);
		
		this.topics.forEach(topic -> {
			((IGGEventsContextSourcedItem) topic).source(source);
		});
		this.dataflows.forEach(dataflow ->{
			((IGGEventsContextSourcedItem) dataflow).source(source);
		});
		this.connectors.forEach(connector -> {
			((IGGEventsContextSourcedItem) connector).source(source);
		});
		this.subscriptions.forEach(subscription -> {
			((IGGEventsContextSourcedItem) subscription).source(source);
		});
		this.locks.forEach(lock -> {
			((IGGEventsContextSourcedItem) lock).source(source);
		});
		this.routes.forEach(route -> {
			((IGGEventsContextSourcedItem) route).source(source);
		});
		return this;
	}

	@Override
	public List<com.garganttua.events.spec.interfaces.context.IGGEventsContextSource> getsources() {
		return this.sources;
	}
}
