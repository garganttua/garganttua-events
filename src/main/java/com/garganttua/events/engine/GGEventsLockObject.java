package com.garganttua.events.engine;

import java.lang.reflect.Method;

import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsDistributedLock;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextLockObject;

public class GGEventsLockObject {

	private IGGEventsDistributedLock distributedLock;
	private IGGEventsContextLockObject ctxtLockObject;

	public GGEventsLockObject(IGGEventsDistributedLock distributedLock, IGGEventsContextLockObject ctxtLockObject) {
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
