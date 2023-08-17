package com.garganttua.events.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistry;

public class GGEventsObjectCreatorRegistry implements IGGEventsObjectRegistry {

	public static final String LABEL = "class";
	
	@Override
	public Object getObject(String ref) throws GGEventsCoreException {
		Object obj = null;
		try {
			Class<?> clazz = Class.forName(ref);
//			if( IGGEventsEnrichStrategy.class.isAssignableFrom(clazz) ) {
				Constructor<?> ctor;
				try {
					ctor = clazz.getDeclaredConstructor();
				} catch (NoSuchMethodException | SecurityException e1) {
					throw new GGEventsCoreException(e1);
				}
				try {
					obj = ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new GGEventsCoreException(e);
				}
//			} else {
//				throw new GGEventsFrameworkException(
//						"The class [" + this.strategyClassName + "] must implements the IGGEventsEnrichStrategy interface.");
//			}
			
		} catch (ClassNotFoundException e) {
			throw new GGEventsCoreException(e);
		}
		return obj;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
