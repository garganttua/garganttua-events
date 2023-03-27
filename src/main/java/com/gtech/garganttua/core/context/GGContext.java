/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context;

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
public class GGContext {
	
	private String tenantId;
	
	private String clusterId; 
		
	@JsonProperty(value ="topics",required = true)
	private List<GGContextTopic> topics;
	
	@JsonProperty(value ="dataflows",required = true)
	private List<GGContextDataFlow> dataflows;
	
	@JsonProperty(value ="subscriptions",required = true)
	private List<GGContextSubscription> subscriptions;
	
	@JsonProperty(value ="connectors",required = true)
	private List<GGContextConnector> connectors;
	
	@JsonProperty(value ="routes",required = true)
	private List<GGContextRoute> routes;
	
	@JsonProperty(value ="distributedLocks",required = false)
	private List<GGContextLock> distributedLocks;

	public void setSource(String assetId, Date now, String source) {
		this.topics.forEach( t -> {t.getSources().add(new GGContextItemSource(assetId, this.clusterId, source, now));});
		this.dataflows.forEach( t -> {t.getSources().add(new GGContextItemSource(assetId, this.clusterId, source, now));});
		this.subscriptions.forEach( t -> {t.getSources().add(new GGContextItemSource(assetId, this.clusterId, source, now));});
		this.connectors.forEach( t -> {t.getSources().add(new GGContextItemSource(assetId, this.clusterId, source, now));});
		this.routes.forEach( t -> {t.getSources().add(new GGContextItemSource(assetId, this.clusterId, source, now));});
		if( this.distributedLocks != null ) {
			this.distributedLocks.forEach(t -> {t.getSources().add(new GGContextItemSource(assetId, this.clusterId, source, now));});
		}
	}

}
