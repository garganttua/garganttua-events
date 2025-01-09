package com.garganttua.events.nativve.image.config;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import com.garganttua.events.connectors.AbstractGGEventsConnector;
import com.garganttua.events.connectors.bus.GGEventsBusConnector;
import com.garganttua.events.context.GGEventsContextDataflowInProcessMode;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextHighAvailabilityMode;
import com.garganttua.events.context.GGEventsContextOriginPolicy;
import com.garganttua.events.context.GGEventsContextPublicationMode;
import com.garganttua.events.context.json.GGEventsJsonContext;
import com.garganttua.events.context.json.GGEventsJsonContextConnector;
import com.garganttua.events.context.json.GGEventsJsonContextConsumerConfiguration;
import com.garganttua.events.context.json.GGEventsJsonContextDataflow;
import com.garganttua.events.context.json.GGEventsJsonContextExceptions;
import com.garganttua.events.context.json.GGEventsJsonContextLock;
import com.garganttua.events.context.json.GGEventsJsonContextLockObject;
import com.garganttua.events.context.json.GGEventsJsonContextProcessor;
import com.garganttua.events.context.json.GGEventsJsonContextProducerConfiguration;
import com.garganttua.events.context.json.GGEventsJsonContextRoute;
import com.garganttua.events.context.json.GGEventsJsonContextSourceItem;
import com.garganttua.events.context.json.GGEventsJsonContextSubscription;
import com.garganttua.events.context.json.GGEventsJsonContextTimeInterval;
import com.garganttua.events.context.json.GGEventsJsonContextTopic;
import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.annotations.GGEventsDistributedLock;
import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.nativve.image.config.NativeImageConfig;
import com.garganttua.nativve.image.config.reflection.IReflectConfigEntryBuilder;
import com.garganttua.nativve.image.config.reflection.ReflectConfig;
import com.garganttua.nativve.image.config.reflection.ReflectConfigEntry;
import com.garganttua.nativve.image.config.reflection.ReflectConfigEntryBuilder;
import com.garganttua.nativve.image.config.resources.ResourceConfig;
import com.garganttua.reflection.utils.GGObjectReflectionHelper;
import com.garganttua.reflection.utils.IGGAnnotationScanner;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NativeImageConfigBuilder {

	static {
		GGObjectReflectionHelper.annotationScanner = new IGGAnnotationScanner() {

			@Override
			public List<Class<?>> getClassesWithAnnotation(String package_, Class<? extends Annotation> annotation) {
				Reflections reflections = new Reflections(package_, Scanners.TypesAnnotated);
				Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotation, true);
				return annotatedClasses.stream().collect(Collectors.toList());
			}
		};
	}

	public static void main(String[] args) throws IOException, NoSuchMethodException, SecurityException {
		createReflectConfig(args[0]);
		createResourceConfig(args[0]);
	}

	private static void createResourceConfig(String path) throws IOException {
		File resourceConfigFile = NativeImageConfig.getResourceConfigFile(path);
		if (!resourceConfigFile.exists())
			resourceConfigFile.createNewFile();

		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContext.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextConnector.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextConsumerConfiguration.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextDataflow.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextExceptions.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextLock.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextLockObject.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextProcessor.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextProducerConfiguration.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextRoute.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextSourceItem.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextSubscription.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextTimeInterval.class);
		ResourceConfig.addResource(resourceConfigFile, GGEventsJsonContextTopic.class);

	}

	private static void createReflectConfig(String path) throws IOException, NoSuchMethodException, SecurityException {
		File reflectConfigFile = NativeImageConfig.getReflectConfigFile(path);
		if (!reflectConfigFile.exists())
			reflectConfigFile.createNewFile();

		ReflectConfig reflectConfig = ReflectConfig.loadFromFile(reflectConfigFile);

		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(AbstractGGEventsConnector.class)
				.constructor(GGEventsBusConnector.class.getDeclaredConstructor()).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(AbstractGGEventsConnector.class.getMethod("setConfiguration", String.class, String.class,
						String.class, String.class, IGGEventsObjectRegistryHub.class, IGGEventsEngine.class))
				.method(AbstractGGEventsConnector.class.getMethod("setName", String.class))
				.method(AbstractGGEventsConnector.class.getMethod("setPoolExecutor", ExecutorService.class)).build());

		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContext.class).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContext.class.getMethod("setTenantId", String.class))
				.method(GGEventsJsonContext.class.getMethod("setClusterId", String.class))
				.method(GGEventsJsonContext.class.getMethod("setConnectors", List.class))
				.method(GGEventsJsonContext.class.getMethod("setDataflows", List.class))
				.method(GGEventsJsonContext.class.getMethod("setLocks", List.class))
				.method(GGEventsJsonContext.class.getMethod("setRoutes", List.class))
				.method(GGEventsJsonContext.class.getMethod("setSubscriptions", List.class))
				.method(GGEventsJsonContext.class.getMethod("setTopics", List.class))
				.constructor(GGEventsJsonContext.class.getDeclaredConstructor()).build());

		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextConnector.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContextConnector.class.getMethod("setConfiguration", String.class))
				.method(GGEventsJsonContextConnector.class.getMethod("setName", String.class))
				.method(GGEventsJsonContextConnector.class.getMethod("setType", String.class))
				.method(GGEventsJsonContextConnector.class.getMethod("setVersion", String.class))
				.constructor(GGEventsJsonContextConnector.class.getDeclaredConstructor()).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextConsumerConfiguration.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContextConsumerConfiguration.class.getMethod("setDestinationPolicy",
						GGEventsContextDestinationPolicy.class))
				.method(GGEventsJsonContextConsumerConfiguration.class.getMethod("setIgnoreAssetMessages",
						boolean.class))
				.method(GGEventsJsonContextConsumerConfiguration.class.getMethod("setOriginPolicy",
						GGEventsContextOriginPolicy.class))
				.method(GGEventsJsonContextConsumerConfiguration.class.getMethod("setProcessMode",
						GGEventsContextDataflowInProcessMode.class))
				.constructor(GGEventsJsonContextConsumerConfiguration.class.getDeclaredConstructor()).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextDataflow.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContextDataflow.class.getMethod("setEncapsulated", boolean.class))
				.method(GGEventsJsonContextDataflow.class.getMethod("setGaranteeOrder", boolean.class))
				.method(GGEventsJsonContextDataflow.class.getMethod("setName", String.class))
				.method(GGEventsJsonContextDataflow.class.getMethod("setType", String.class))
				.method(GGEventsJsonContextDataflow.class.getMethod("setUuid", String.class))
				.method(GGEventsJsonContextDataflow.class.getMethod("setVersion", String.class))
				.constructor(GGEventsJsonContextDataflow.class.getDeclaredConstructor()).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextExceptions.class)
				.constructor(GGEventsJsonContextExceptions.class.getDeclaredConstructor()).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextLock.class)
				.constructor(GGEventsJsonContextLock.class.getDeclaredConstructor()).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextLockObject.class)
				.constructor(GGEventsJsonContextLockObject.class.getDeclaredConstructor()).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextProcessor.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContextProcessor.class.getMethod("setConfiguration", String.class))
				.method(GGEventsJsonContextProcessor.class.getMethod("setType", String.class))
				.method(GGEventsJsonContextProcessor.class.getMethod("setVersion", String.class))
				.constructor(GGEventsJsonContextProcessor.class.getDeclaredConstructor()).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextProducerConfiguration.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContextProducerConfiguration.class.getMethod("setDestinationPolicy",
						GGEventsContextDestinationPolicy.class))
				.constructor(GGEventsJsonContextProducerConfiguration.class.getDeclaredConstructor()).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextRoute.class).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContextRoute.class.getMethod("setFrom", String.class))
				.method(GGEventsJsonContextRoute.class.getMethod("setProcessors", List.class))
				.method(GGEventsJsonContextRoute.class.getMethod("setTo", String.class))
				.method(GGEventsJsonContextRoute.class.getMethod("setUuid", String.class))
				.constructor(GGEventsJsonContextRoute.class.getDeclaredConstructor()).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextSourceItem.class)
				.constructor(GGEventsJsonContextSourceItem.class.getDeclaredConstructor()).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextSubscription.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContextSubscription.class.getMethod("setConnector", String.class))
				.method(GGEventsJsonContextSubscription.class.getMethod("setConsumerConfiguration",
						GGEventsJsonContextConsumerConfiguration.class))
				.method(GGEventsJsonContextSubscription.class.getMethod("setDataflow", String.class))
				.method(GGEventsJsonContextSubscription.class.getMethod("setProducerConfiguration",
						GGEventsJsonContextProducerConfiguration.class))
				.method(GGEventsJsonContextSubscription.class.getMethod("setPublicationMode",
						GGEventsContextPublicationMode.class))
				.method(GGEventsJsonContextSubscription.class.getMethod("setTopic", String.class))
				.constructor(GGEventsJsonContextSubscription.class.getDeclaredConstructor()).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextTimeInterval.class)
				.constructor(GGEventsJsonContextTimeInterval.class.getDeclaredConstructor()).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsJsonContextTopic.class).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(GGEventsJsonContextTopic.class.getMethod("setRef", String.class))
				.constructor(GGEventsJsonContextTopic.class.getDeclaredConstructor()).build());

		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsContextDataflowInProcessMode.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsContextDestinationPolicy.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsContextHighAvailabilityMode.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsContextOriginPolicy.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());
		reflectConfig.addEntry(ReflectConfigEntryBuilder.builder(GGEventsContextPublicationMode.class)
				.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true).build());

		reflectConfig.saveToFile(reflectConfigFile);
	}

	public static void createConfiguration(String pathToConfiguration, List<String> packages) throws IOException {

		File reflectConfigFile = NativeImageConfig.getReflectConfigFile(pathToConfiguration);
		if (!reflectConfigFile.exists()) {
			log.info("Creation of reflection configuration in directory " + pathToConfiguration);
			reflectConfigFile.createNewFile();
		}

		File resourceConfigFile = NativeImageConfig.getResourceConfigFile(pathToConfiguration);
		if (!resourceConfigFile.exists()) {
			log.info("Creation of resources configuration in directory " + pathToConfiguration);
			resourceConfigFile.createNewFile();
		}

		ReflectConfig reflectConfig = ReflectConfig.loadFromFile(reflectConfigFile);

		List<Class<?>> connectorsClasses = new ArrayList<Class<?>>();
		List<Class<?>> processorsClasses = new ArrayList<Class<?>>();
		List<Class<?>> contextSourcesClasses = new ArrayList<Class<?>>();
		List<Class<?>> locksClasses = new ArrayList<Class<?>>();

		packages.forEach(p -> {
			log.info("Scanning package {}", p);
			connectorsClasses.addAll(GGObjectReflectionHelper.getClassesWithAnnotation(p, GGEventsConnector.class));
			processorsClasses.addAll(GGObjectReflectionHelper.getClassesWithAnnotation(p, GGEventsProcessor.class));
			contextSourcesClasses
					.addAll(GGObjectReflectionHelper.getClassesWithAnnotation(p, GGEventsContextSource.class));
			locksClasses.addAll(GGObjectReflectionHelper.getClassesWithAnnotation(p, GGEventsDistributedLock.class));
		});

		processClasses(reflectConfig, resourceConfigFile, connectorsClasses,
				NativeImageConfigBuilder::processConnectorClass);
		processClasses(reflectConfig, resourceConfigFile, processorsClasses,
				NativeImageConfigBuilder::processProcessorClass);
		processClasses(reflectConfig, resourceConfigFile, contextSourcesClasses,
				NativeImageConfigBuilder::processContextSourceClass);
		processClasses(reflectConfig, resourceConfigFile, locksClasses, NativeImageConfigBuilder::processLockClass);

		log.info("Writing file in directory " + pathToConfiguration);
		reflectConfig.saveToFile(reflectConfigFile);
	}

	private static void processClasses(ReflectConfig reflectConfig, File resourceConfigFile,
			List<Class<?>> entityClasses, ClassProcessorInterface processor) {
		entityClasses.forEach(entityClass -> {
			try {
				ResourceConfig.addResource(resourceConfigFile, entityClass);
				reflectConfig.addEntry(processor.processClass(reflectConfig, entityClass));
			} catch (NoSuchMethodException | SecurityException e) {
				log.warn("Error", e);
			} catch (IOException e) {
				log.warn("Error", e);
			}
		});
	}

	private static ReflectConfigEntry processConnectorClass(ReflectConfig reflectConfig, Class<?> entityClass)
			throws NoSuchMethodException, SecurityException {
		log.info("Processing connector " + entityClass.getSimpleName());
		IReflectConfigEntryBuilder entryBuilder = getReflectConfigEntryBuilder(reflectConfig, entityClass);

		entryBuilder.constructor(entityClass.getDeclaredConstructor()).allDeclaredFields(true)
				.queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true);

		return entryBuilder.build();
	}

	private static ReflectConfigEntry processProcessorClass(ReflectConfig reflectConfig, Class<?> entityClass)
			throws NoSuchMethodException, SecurityException {
		log.info("Processing processor " + entityClass.getSimpleName());
		IReflectConfigEntryBuilder entryBuilder = getReflectConfigEntryBuilder(reflectConfig, entityClass);

		entryBuilder.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(entityClass.getMethod("setConfiguration", String.class, String.class, String.class,
						String.class, IGGEventsObjectRegistryHub.class, IGGEventsEngine.class))
				.method(entityClass.getMethod("setExecutorService", ExecutorService.class))
				.method(entityClass.getMethod("setScheduledExecutorService", ScheduledExecutorService.class))
				.constructor(entityClass.getDeclaredConstructor());

		return entryBuilder.build();
	}

	private static ReflectConfigEntry processContextSourceClass(ReflectConfig reflectConfig, Class<?> entityClass)
			throws NoSuchMethodException, SecurityException {
		log.info("Processing context source " + entityClass.getSimpleName());
		IReflectConfigEntryBuilder entryBuilder = getReflectConfigEntryBuilder(reflectConfig, entityClass);

		entryBuilder.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.method(entityClass.getMethod("getConfiguration")).method(entityClass.getMethod("getType"))
				.method(entityClass.getMethod("readContext")).method(entityClass.getMethod("readContext", String.class))
				.method(entityClass.getMethod("readContext", String.class, boolean.class))
				.method(entityClass.getMethod("setConfiguration", String.class))
				.method(entityClass.getMethod("writeContext", IGGEventsContext.class))
				.method(entityClass.getMethod("writeContext", IGGEventsContext.class, String.class))
				.method(entityClass.getMethod("writeContext", IGGEventsContext.class, String.class, boolean.class))
				.constructor(entityClass.getDeclaredConstructor());

		return entryBuilder.build();
	}

	private static ReflectConfigEntry processLockClass(ReflectConfig reflectConfig, Class<?> entityClass)
			throws NoSuchMethodException, SecurityException {
		log.info("Processing lock " + entityClass.getSimpleName());
		IReflectConfigEntryBuilder entryBuilder = getReflectConfigEntryBuilder(reflectConfig, entityClass);

		entryBuilder.allDeclaredFields(true).queryAllDeclaredConstructors(true).queryAllDeclaredMethods(true)
				.constructor(entityClass.getDeclaredConstructor())
				.method(entityClass.getMethod("setConfiguration", String.class, String.class, String.class,
						String.class, IGGEventsObjectRegistryHub.class, IGGEventsEngine.class))
				.method(entityClass.getMethod("setName", String.class));

		return entryBuilder.build();
	}

	private static IReflectConfigEntryBuilder getReflectConfigEntryBuilder(ReflectConfig reflectConfig,
			Class<?> entityClass) {
		IReflectConfigEntryBuilder entryBuilder = null;
		Optional<ReflectConfigEntry> entry__ = reflectConfig.findEntryByName(entityClass);
		if (entry__.isPresent()) {
			entryBuilder = ReflectConfigEntryBuilder.builder(entry__.get());
		} else {
			entryBuilder = ReflectConfigEntryBuilder.builder(entityClass);
		}
		return entryBuilder;
	}
}