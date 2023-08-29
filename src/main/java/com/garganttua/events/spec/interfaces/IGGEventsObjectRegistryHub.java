package com.garganttua.events.spec.interfaces;


import com.garganttua.events.spec.exceptions.GGEventsException;

public interface IGGEventsObjectRegistryHub {

	void addObjectRegistry(String label, IGGEventsObjectRegistry registry);

	Object getObject(String label) throws GGEventsException;
	
}
