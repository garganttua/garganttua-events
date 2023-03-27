/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 
 * @author 
 * 
 * 
 * "dataFlow":{
      "uuid":"",
      "name":"",
      "type":"", 
      "garanteeOrder":"",
      "version": ""
   }
 * 
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class GGContextDataFlow extends GGSourcedContextItem {
	
	public GGContextDataFlow(String uuid, String name, String type, boolean garanteeOrder, String version, boolean encapsulated, List<GGContextItemSource> sources) {
		super(sources);
		this.uuid = uuid;
		this.name = name;
		this.type = type;
		this.garanteeOrder = garanteeOrder;
		this.version = version;
		this.encapsulated = encapsulated;
	}

	@JsonProperty(value ="uuid",required = true)
	protected String uuid;
	
	@JsonProperty(value ="name",required = true)
	protected String name;
	
	@JsonProperty(value ="type",required = true)
	protected String type;
	
	@JsonProperty(value ="garanteeOrder", required = true)
	protected boolean garanteeOrder;
	
	@JsonProperty(value ="version", required = true)
	protected String version;
	
	@JsonProperty(value ="encapsulated", required = true)
	protected boolean encapsulated;
	
	@JsonIgnore
	public String getMinorVersion() {
		String[] splitted = this.version.split("\\.");
		return splitted[1];
	}
	
	@JsonIgnore
	public String getMajorVersion() {
		String[] splitted = this.version.split("\\.");
		return splitted[0];
	}
	
}
