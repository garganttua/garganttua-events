package com.gtech.garganttua.core.engine;

import java.lang.reflect.Method;

import com.gtech.garganttua.core.context.GGContextLockObject;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGDistributedLock;

public class GGLockObject {

	private IGGDistributedLock distributedLock;
	private GGContextLockObject ctxtLockObject;

	public GGLockObject(IGGDistributedLock distributedLock, GGContextLockObject ctxtLockObject) {
		this.distributedLock = distributedLock;
		this.ctxtLockObject = ctxtLockObject;
	}

	public void doSynchronously(Object object, Method method, Object[] args) throws GGCoreProcessingException {
		this.distributedLock.doSynchronously(ctxtLockObject.getLockObject(), object, method, args);
	}

	public void start() {
		this.distributedLock.start();
	}

}
