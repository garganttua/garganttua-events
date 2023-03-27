package com.gtech.garganttua.core.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistry;

public class GGObjectCreatorRegistry implements IGGObjectRegistry {

	@Override
	public Object getObject(String ref) throws GGCoreException {
		Object obj = null;
		try {
			Class<?> clazz = Class.forName(ref);
//			if( IGGEnrichStrategy.class.isAssignableFrom(clazz) ) {
				Constructor<?> ctor;
				try {
					ctor = clazz.getDeclaredConstructor();
				} catch (NoSuchMethodException | SecurityException e1) {
					throw new GGCoreException(e1);
				}
				try {
					obj = ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new GGCoreException(e);
				}
//			} else {
//				throw new GGFrameworkException(
//						"The class [" + this.strategyClassName + "] must implements the IGGEnrichStrategy interface.");
//			}
			
		} catch (ClassNotFoundException e) {
			throw new GGCoreException(e);
		}
		return obj;
	}

}
