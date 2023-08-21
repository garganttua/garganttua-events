package com.garganttua.events.spec.interfaces;


import com.garganttua.events.spec.exceptions.GGEventsCoreException;

public interface IGGEventsObjectRegistry {

	Object getObject(String ref) throws GGEventsCoreException;
	
	String getLabel();

}
