package com.garganttua.events.spec.interfaces.context;

public interface IGGEventsContextDataflow extends IGGEventsSourcedContextItem {

	String getUuid();

	String getName();

	String getType();

	String getVersion();

	boolean isEncapsulated();
	
	boolean isGaranteeOrder();

}
