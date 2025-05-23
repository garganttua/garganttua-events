package com.garganttua.events.engine;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.objects.GGEventsUtils;
import com.garganttua.reflection.utils.GGObjectReflectionHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsContextAnnotateClassesRegistry {

	public static Map<String, Map<String, Class<?>>> findClassesWithAnnotationAndInterface(String packageName, Class<? extends Annotation> annotation, Class<?> implementedInterface, Map<String, Map<String, Class<?>>> annotateClasses) throws GGEventsException{
		log.info("Scanning package " + packageName+ " for annotation "+annotation.getSimpleName()+ " implementing interface "+implementedInterface.getSimpleName());

		List<Class<?>> sources = GGObjectReflectionHelper.getClassesWithAnnotation(packageName, annotation);

		log.info(" -> Found " + sources.size() + " annotate classes");
		for (Class<?> clazz : sources) {
			
			if (!implementedInterface.isAssignableFrom(clazz)) {
				throw new GGEventsException("The class [" + clazz.getName() + "] must implements the "+implementedInterface.getName()+" interface.");
			}
			
			 Annotation annotation__ = clazz.getAnnotation(annotation);
			 
			 try {
				Method typeMethod = annotation.getDeclaredMethod("type");
				String type = (String) typeMethod.invoke(annotation__);
				
				Method versionMethod = annotation.getDeclaredMethod("version");
				String version = (String) versionMethod.invoke(annotation__);
				
				GGEventsUtils.checkVersion(version);
				
				Map<String, Class<?>> versions = annotateClasses.get(type);
				
				if( versions == null ) {
					versions = new HashMap<String, Class<?>>();
					annotateClasses.put(type, versions);
				}
				
				log.info(" -> Annotate class registered : " + type+" with version "+version);

				versions.put(version, clazz);
				
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
				throw new GGEventsException(e);
			}

		}
		return annotateClasses;
	}
	
}
