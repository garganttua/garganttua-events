/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;


import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;

import lombok.Getter;


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
public class GGEventsContextDataflow extends GGEventsContextItem<GGEventsContextDataflow> implements IGGEventsContextDataflow {

	public GGEventsContextDataflow(String uuid, String name, String type, boolean garanteeOrder, String version, boolean encapsulated) {
		this.uuid = uuid;
		this.name = name;
		this.type = type;
		this.garanteeOrder = garanteeOrder;
		this.version = version;
		this.encapsulated = encapsulated;
	}

	@Getter
	protected String uuid;
	
	@Getter
	protected String name;
	
	@Getter
	protected String type;
	
	@Getter
	protected boolean garanteeOrder;
	
	@Getter
	protected String version;
	
	@Getter
	protected boolean encapsulated;
//	
//	@JsonIgnore
//	public String getMinorVersion() {
//		String[] splitted = this.version.split("\\.");
//		return splitted[1];
//	}
//	
//	@JsonIgnore
//	public String getMajorVersion() {
//		String[] splitted = this.version.split("\\.");
//		return splitted[0];
//	}

	@Override
	public boolean equals(Object dataflow) {
		return ((GGEventsContextDataflow) dataflow).uuid.equals(this.uuid);
	}
	
	@Override
	public int hashCode() {
		return this.uuid.hashCode();
	}

	@Override
	protected boolean isEqualTo(GGEventsContextDataflow item) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
