/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsContext {
	
	private String tenantId;
	
	private String clusterId; 
		
	@JsonProperty(value ="topics",required = true)
	private List<GGEventsContextTopic> topics;
	
	@JsonProperty(value ="dataflows",required = true)
	private List<GGEventsContextDataFlow> dataflows;
	
	@JsonProperty(value ="subscriptions",required = true)
	private List<GGEventsContextSubscription> subscriptions;
	
	@JsonProperty(value ="connectors",required = true)
	private List<GGEventsContextConnector> connectors;
	
	@JsonProperty(value ="routes",required = true)
	private List<GGEventsContextRoute> routes;
	
	@JsonProperty(value ="distributedLocks",required = false)
	private List<GGEventsContextLock> distributedLocks;

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

}
