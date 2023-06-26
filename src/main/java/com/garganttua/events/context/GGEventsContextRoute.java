/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GGEventsContextRoute extends GGEventsSourcedContextItem {
	
	public GGEventsContextRoute(String uuid, String from, Map<Integer, GGEventsContextProcessor> processors, String to, List<GGEventsContextItemSource> sources, GGEventsContextExceptions exceptions, GGEventsContextLockObject synchronization) {
		super(sources);
		this.uuid = uuid;
		this.from = from;
		this.processors = processors;
		this.to = to;
		this.exceptions = exceptions;
		this.synchronization = synchronization;
	}

	@JsonProperty(value ="uuid",required = true)
	private String uuid;
	
	@JsonProperty(value ="from",required = true)
	private String from;
	
	@JsonProperty(value ="processors",required = true)
	private Map<Integer, GGEventsContextProcessor> processors;
	
	@JsonProperty(value ="to",required = true)
	private String to;
	
	@JsonProperty
	private GGEventsContextExceptions exceptions;
	
	@JsonProperty
	private GGEventsContextLockObject synchronization;

}
