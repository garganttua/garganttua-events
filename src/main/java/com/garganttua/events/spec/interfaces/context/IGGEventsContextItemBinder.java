package com.garganttua.events.spec.interfaces.context;

import com.garganttua.events.spec.exceptions.GGEventsException;

public interface IGGEventsContextItemBinder<T> {
	
	T bind() throws GGEventsException;
	
	void build(T item) throws GGEventsException;

}
