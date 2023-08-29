package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextLock;

import lombok.Getter;

public class GGEventsContextLock extends GGEventsContextItem<GGEventsContextLock> implements IGGEventsContextLock {
	
	public GGEventsContextLock(String name, String type, String version, String configuration, List<GGEventsContextItemSource> sources) {
		this.sources.addAll(sources);
		this.name = name;
		this.type = type;
		this.version = version;
		this.configuration = configuration;
	}

	public GGEventsContextLock(String name, String type, String version, String configuration) {
		this(name, type, version, configuration, new ArrayList<GGEventsContextItemSource>());
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
