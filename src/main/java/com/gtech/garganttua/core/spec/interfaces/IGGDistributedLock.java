package com.gtech.garganttua.core.spec.interfaces;

import java.lang.reflect.Method;

import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;

public interface IGGDistributedLock extends IGGConfigurable, IGGDescribable {

	void doSynchronously(String lockObject, Object object, Method method, Object[] args) throws GGCoreProcessingException;

	void start();

}
