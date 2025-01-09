package com.garganttua.events.spec.interfaces.context;

import java.util.List;

public interface IGGEventsContextSourcedItem {
	
	List<IGGEventsContextSource> getsources();
	
	void source(IGGEventsContextSource source);
	
	void otherVersion(IGGEventsContextSourcedItem version);
	
	List<?> getOtherVersions();

}
