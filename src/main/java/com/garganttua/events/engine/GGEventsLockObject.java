package com.garganttua.events.engine;

import java.lang.reflect.Method;

import com.garganttua.events.context.GGEventsContextLockObject;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsDistributedLock;

public class GGEventsLockObject {

	private IGGEventsDistributedLock distributedLock;
	private GGEventsContextLockObject ctxtLockObject;

	public GGEventsLockObject(IGGEventsDistributedLock distributedLock, GGEventsContextLockObject ctxtLockObject) {
		this.distributedLock = distributedLock;
		this.ctxtLockObject = ctxtLockObject;
	}

	public void doSynchronously(Object object, Method method, Object[] args) throws GGEventsProcessingException {
		this.distributedLock.doSynchronously(ctxtLockObject.getLockObject(), object, method, args);
	}

	public void start() {
		this.distributedLock.start();
	}

}
