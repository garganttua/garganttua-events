package com.garganttua.events.spec.objects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;

public class GGEventsUtils {
	

	public static <T> T getInstanceOf(Class<T> source) throws GGEventsException {
		try {
			Constructor<T> declaredConstructors = source.getDeclaredConstructor();
			return declaredConstructors.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new GGEventsException(e);
		}
	}
	
	public static void checkVersion(String version) throws GGEventsException {
		String[] splitted = version.split("\\.");
		
		if( splitted.length != 2 ) {
			throw new GGEventsException("The version "+version+" is incorrect, must be x.y format, with x and y integers");
		}
		try { 
			Integer.valueOf(splitted[0]);
			Integer.valueOf(splitted[1]);
		} catch( Exception e ) {
			throw new GGEventsException("The version "+version+" is incorrect, must be x.y format, with x and y integers");
		}
	}
	
	public static IGGEventsContextSource getSourceObj(String type, String version, Map<String, Map<String, Class<?>>> sources) throws GGEventsException {
		Class<?> source = (Class<?>) sources.get(type).get(version);
		if( source == null ) {
			throw new GGEventsException("Context source of type "+type+" and version "+version+" is not found");
		}
		
		GGEventsUtils.checkVersion(version);
		
		IGGEventsContextSource source__;
		source__ = (IGGEventsContextSource) GGEventsUtils.getInstanceOf(source);

		return source__;
	}

}
