package com.garganttua.events.spec.interfaces;


import com.garganttua.events.spec.exceptions.GGEventsException;

public interface IGGEventsObjectRegistry {

	Object getObject(String ref) throws GGEventsException;
	
	String getLabel();

}
