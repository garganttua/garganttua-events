/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextProcessor {

	@JsonProperty(value ="type",required = true)
	private String type;
	
	@JsonProperty(value ="version", required = true)
	private String version;
	
	@JsonProperty(value ="configuration",required = true)
	@JsonDeserialize(using = StupidValueDeserializer.class)
//	@JsonSerialize(using = StupidValueSerializer.class)
	private String configuration;

	public String uuid;
	
}
