package com.garganttua.events.spec.interfaces;

import java.lang.reflect.Method;

import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;

public interface IGGEventsDistributedLock extends IGGEventsConfigurable, IGGEventsDescribable {

	void doSynchronously(String lockObject, Object object, Method method, Object[] args) throws GGEventsCoreProcessingException;

	void start();

}
