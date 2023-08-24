package com.garganttua.events.engine;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.IGGEventsContextSource;

public class GGEventsContextSourcesRegistry {

	public static List<IGGEventsContextSource> findAvailableSources(String packageName) {
		return new ArrayList<IGGEventsContextSource>();
	}

}
