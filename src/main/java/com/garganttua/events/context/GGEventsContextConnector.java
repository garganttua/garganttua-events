/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextConnector;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GGEventsContextConnector extends GGEventsContextItem<GGEventsContextConnector> implements IGGEventsContextConnector {
	
	public GGEventsContextConnector(String name, String type, String version, String configuration) {
		this.name = name;
		this.type = type;
		this.configuration = configuration;
		this.version = version;
	}

	private String name; 
	
	private String type; 
	
	private String version;
	
	private String configuration;

	@Override
	public boolean equals(Object obj) {
		GGEventsContextConnector item = (GGEventsContextConnector) obj;
		return this.name.equals(item.getName());
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	protected boolean isEqualTo(GGEventsContextConnector item) {
		return this.equals(item) 
				&& this.type.equals(item.getType())
				&& this.version.equals(item.getVersion())
				&&this.configuration.equals(item.getConfiguration());
	}

}
