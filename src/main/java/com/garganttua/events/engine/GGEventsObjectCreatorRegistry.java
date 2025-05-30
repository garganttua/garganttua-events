package com.garganttua.events.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistry;

public class GGEventsObjectCreatorRegistry implements IGGEventsObjectRegistry {

	public static final String LABEL = "class";
	
	@Override
	public Object getObject(String ref) throws GGEventsException {
		Object obj = null;
		try {
			Class<?> clazz = Class.forName(ref);
//			if( IGGEventsEnrichStrategy.class.isAssignableFrom(clazz) ) {
				Constructor<?> ctor;
				try {
					ctor = clazz.getDeclaredConstructor();
				} catch (NoSuchMethodException | SecurityException e1) {
					throw new GGEventsException(e1);
				}
				try {
					obj = ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new GGEventsException(e);
				}
//			} else {
//				throw new GGEventsFrameworkException(
//						"The class [" + this.strategyClassName + "] must implements the IGGEventsEnrichStrategy interface.");
//			}
			
		} catch (ClassNotFoundException e) {
			throw new GGEventsException(e);
		}
		return obj;
	}

	@Override
	public String getLabel() {
		return null;
	}

}
