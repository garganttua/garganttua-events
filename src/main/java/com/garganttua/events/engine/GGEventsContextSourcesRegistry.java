package com.garganttua.events.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsContextSourcesRegistry {

	public static Map<String, IGGEventsContextSource> findAvailableSources(String packageName) throws GGEventsException {
		Map<String, IGGEventsContextSource> contextSources = new HashMap<String, IGGEventsContextSource>();
		log.info("Scanning package " + packageName);
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> sources = reflections.getTypesAnnotatedWith(GGEventsContextSource.class);


		log.info(" -> Found " + sources.size() + " context sources");
		for (Class<?> clazz : sources) {

			if (!IGGEventsContextSource.class.isAssignableFrom(clazz)) {
				throw new GGEventsException(
						"The class [" + clazz.getName() + "] must implements the IGGEventsContextSource interface.");
			}

			GGEventsContextSource contextSourceAnnotation = clazz.getAnnotation(GGEventsContextSource.class);

			Constructor<?> toto;
			try {
				toto = clazz.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGEventsException(e1);
			}
			IGGEventsContextSource contextSource = null;
			try {
				contextSource = (IGGEventsContextSource) toto.newInstance();
				contextSources.put(contextSourceAnnotation.name(), contextSource);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGEventsException(e);
			}

			log.info(" -> Context source registered : " + contextSourceAnnotation.name());
		}
		
		return contextSources;
	}

}
