package com.garganttua.events.spec.interfaces.context;

import java.util.List;

import com.garganttua.events.context.GGEventsContextItemSource;

public interface IGGEventsSourcedContextItem {
	
	List<IGGEventsContextItemSource> getsources();
	
	void source(IGGEventsContextItemSource source);

}
