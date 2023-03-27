/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GGContextRoute extends GGSourcedContextItem {
	
	public GGContextRoute(String uuid, String from, Map<Integer, GGContextProcessor> processors, String to, List<GGContextItemSource> sources, GGContextExceptions exceptions, GGContextLockObject synchronization) {
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
	private Map<Integer, GGContextProcessor> processors;
	
	@JsonProperty(value ="to",required = true)
	private String to;
	
	@JsonProperty
	private GGContextExceptions exceptions;
	
	@JsonProperty
	private GGContextLockObject synchronization;

}
