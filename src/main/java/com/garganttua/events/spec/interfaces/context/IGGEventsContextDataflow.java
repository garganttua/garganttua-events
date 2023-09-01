package com.garganttua.events.spec.interfaces.context;

public interface IGGEventsContextDataflow extends IGGEventsContextSourcedItem, IGGEventsContextMergeableItem<IGGEventsContextDataflow>{

	String getUuid();

	String getName();

	String getType();

	String getVersion();

	boolean isEncapsulated();
	
	boolean isGaranteeOrder();

}
