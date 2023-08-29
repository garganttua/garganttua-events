package com.garganttua.events.spec.interfaces;

import java.lang.reflect.Method;

import com.garganttua.events.spec.exceptions.GGEventsProcessingException;

public interface IGGEventsDistributedLock extends IGGEventsConfigurable, IGGEventsDescribable, IGGEventsNamable {

	void doSynchronously(String lockObject, Object object, Method method, Object[] args) throws GGEventsProcessingException;

	void start();

}
