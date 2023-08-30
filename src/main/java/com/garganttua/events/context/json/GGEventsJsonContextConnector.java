package com.garganttua.events.context.json;

import com.garganttua.events.context.GGEventsContextConnector;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConnector;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextConnector implements IGGEventsContextItemBinder<IGGEventsContextConnector> {

	private String name; 
	
	private String type; 
	
	private String version;
	
	private String configuration;

	@Override
	public void build(IGGEventsContextConnector bound) {
		this.name = bound.getName();
		this.type = bound.getType();
		this.version = bound.getVersion();
		this.configuration = bound.getConfiguration();
	}

	@Override
	public IGGEventsContextConnector bind() {
		return new GGEventsContextConnector(this.name, this.type, this.version, this.configuration);
	}

}
