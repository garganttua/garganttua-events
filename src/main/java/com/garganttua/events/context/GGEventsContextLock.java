package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextLock;

import lombok.Getter;

public class GGEventsContextLock extends GGEventsContextSourcedItem<IGGEventsContextLock> implements IGGEventsContextLock {
	
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
	private String configuration;
	
	@Override
	public boolean equals(Object obj) {
		GGEventsContextLock item = (GGEventsContextLock) obj;
		return this.name.equals(item.getName());
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	protected boolean isEqualTo(IGGEventsContextLock item) {
		return this.equals(item) && this.configuration.equals(item.getConfiguration()) && this.type.equals(item.getType())
				&& this.version.equals(item.getVersion());
	}

}
