/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextRoute;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class GGEventsContext implements IGGEventsContext {
	
	private String tenantId;
	
	private String clusterId; 
		
	private List<GGEventsContextTopic> topics = new ArrayList<GGEventsContextTopic>();
	
	private List<GGEventsContextDataFlow> dataflows = new ArrayList<GGEventsContextDataFlow>();
	
	private List<GGEventsContextSubscription> subscriptions = new ArrayList<GGEventsContextSubscription>();

	private List<GGEventsContextConnector> connectors = new ArrayList<GGEventsContextConnector>();

	private List<GGEventsContextRoute> routes = new ArrayList<GGEventsContextRoute>();

	private List<GGEventsContextLock> distributedLocks = new ArrayList<GGEventsContextLock>();

	public GGEventsContext(String tenantId, String clusterId) {
		this.clusterId = clusterId;
		this.tenantId = tenantId;
	}

	public void setSource(String assetId, Date now, String source) {
		this.topics.forEach( t -> {t.getSources().add(new GGEventsContextItemSource(assetId, this.clusterId, source, now));});
		this.dataflows.forEach( t -> {t.getSources().add(new GGEventsContextItemSource(assetId, this.clusterId, source, now));});
		this.subscriptions.forEach( t -> {t.getSources().add(new GGEventsContextItemSource(assetId, this.clusterId, source, now));});
		this.connectors.forEach( t -> {t.getSources().add(new GGEventsContextItemSource(assetId, this.clusterId, source, now));});
		this.routes.forEach( t -> {t.getSources().add(new GGEventsContextItemSource(assetId, this.clusterId, source, now));});
		if( this.distributedLocks != null ) {
			this.distributedLocks.forEach(t -> {t.getSources().add(new GGEventsContextItemSource(assetId, this.clusterId, source, now));});
		}
	}

	@Override
	public IGGEventsEngine build() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsContext topic(String ref) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsContext dataflow(String uuid, String name, String type, boolean garanteeOrder, String version,
			boolean encapsulated) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsContextSubscription subscription(String dataflowUuid, String topicRef, String connectorName,
			GGEventsContextPublicationMode publicationMode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsContext connector(String name, String type, String version, String configuration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsContextRoute route(String string, String string2, String string3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsContext lock(String name, String type, String version, String configuration) {
		// TODO Auto-generated method stub
		return null;
	}
}
