package com.garganttua.events.spec.interfaces.context;

import java.util.List;
import java.util.Map;

public interface IGGEventsContextRoute extends IGGEventsContextSourcedItem, IGGEventsContextMergeableItem<IGGEventsContextRoute>, IGGEventsContextItemLinkedToContext {

	IGGEventsContextRoute processor(String type, String version, String configuration);

	IGGEventsContextRoute exceptions(String to, String cast, String label);

	IGGEventsContextRoute synchronization(String lock, String lockObject);
	
	IGGEventsContextRoute processor(IGGEventsContextProcessor processor);

	IGGEventsContextRoute exceptions(IGGEventsContextExceptions exception);

	IGGEventsContextRoute synchronization(IGGEventsContextLockObject synchronization);

	String getFrom();

	String getTo();

	String getUuid();

	IGGEventsContextExceptions getExceptions();

	List<IGGEventsContextProcessor> getProcessors();

	IGGEventsContextLockObject getSynchronization();

}
