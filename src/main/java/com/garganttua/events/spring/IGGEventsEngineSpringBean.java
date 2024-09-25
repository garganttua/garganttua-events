package com.garganttua.events.spring;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;

public interface IGGEventsEngineSpringBean {

	void start() throws GGEventsException;
	
	void stop() throws GGEventsException;
	
	void reload() throws GGEventsException;
	
	void init() throws GGEventsException;
	
	void flush() throws GGEventsException;
	
	IGGEventsEngine getEngine();

}
