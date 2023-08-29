package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextLock;

import lombok.Getter;

public class GGEventsContextLock extends GGEventsContextItem<GGEventsContextLock> implements IGGEventsContextLock {
	
	public GGEventsContextLock(String name, String type, String version, String configuration) {
		this.name = name;
		this.type = type;
		this.version = version;
		this.configuration = configuration;
	}

	@Getter
	private String name;
	
	@Getter
	private String type;
	
	@Getter
	private String version;
	
	@Getter
	private String configuration = "";

	@Override
	protected boolean isEqualTo(GGEventsContextLock item) {
		// TODO Auto-generated method stub
		return false;
	}

}
