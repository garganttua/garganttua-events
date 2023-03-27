/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GGContextConnector extends GGSourcedContextItem {
	
	public GGContextConnector(String name, String type, String configuration, List<GGContextItemSource> sources, String version) {
		super(sources);
		this.name = name;
		this.type = type;
		this.configuration = configuration;
		this.version = version;
	}

	@JsonProperty(value ="name", required = true)
	private String name; 
	
	@JsonProperty(value ="type", required = true)
	private String type; 
	
	@JsonProperty(value ="version", required = true)
	private String version;
	
	@JsonProperty(value ="configuration",required = true)
	@JsonDeserialize(using = StupidValueDeserializer.class)
//	@JsonSerialize(using = StupidValueSerializer.class)
	private String configuration = "";

}
