/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextConnector;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GGEventsContextConnector extends GGEventsContextItem<GGEventsContextConnector> implements IGGEventsContextConnector {
	
	public GGEventsContextConnector(String name, String type, String configuration, String version, List<GGEventsContextItemSource> sources) {
		this.sources.addAll(sources);
		this.name = name;
		this.type = type;
		this.configuration = configuration;
		this.version = version;
	}

	public GGEventsContextConnector(String name, String type, String configuration, String version) {
		this(name, type, configuration, version, new ArrayList<GGEventsContextItemSource>());
	}

	private String name; 
	
	private String type; 
	
	private String version;
	
	private String configuration = "";

	@Override
	protected boolean isEqualTo(GGEventsContextConnector item) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
