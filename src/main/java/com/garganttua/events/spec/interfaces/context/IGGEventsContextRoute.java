package com.garganttua.events.spec.interfaces.context;

public interface IGGEventsContextRoute extends IGGEventsContextItemLinkedToContext {

	IGGEventsContextRoute processor(String type, String version, String configuration);

	IGGEventsContextRoute exceptions(String to, String cast, String label);

	IGGEventsContextRoute synchronization(String lock, String lockObject);
	
	IGGEventsContextRoute processor(IGGEventsContextProcessor processor);

	IGGEventsContextRoute exceptions(IGGEventsContextExceptions exception);

	IGGEventsContextRoute synchronization(IGGEventsContextLockObject synchronization);

}
