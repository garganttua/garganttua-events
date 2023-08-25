package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextLock;

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

	private String name;
	
	private String type;
	
	private String version;
	
	private String configuration = "";

	@Override
	protected boolean isEqualTo(GGEventsContextLock item) {
		// TODO Auto-generated method stub
		return false;
	}

}
