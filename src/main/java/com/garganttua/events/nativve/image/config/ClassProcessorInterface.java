package com.garganttua.events.nativve.image.config;

import com.garganttua.nativve.image.config.reflection.ReflectConfig;
import com.garganttua.nativve.image.config.reflection.ReflectConfigEntry;

@FunctionalInterface
public interface ClassProcessorInterface {

	ReflectConfigEntry processClass(ReflectConfig reflectConfig, Class<?> entityClass) throws NoSuchMethodException, SecurityException;

}
