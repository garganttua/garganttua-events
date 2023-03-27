/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;

import com.gtech.garganttua.core.connectors.core.events.GGCoreEventsConnector;
import com.gtech.garganttua.core.context.GGContext;
import com.gtech.garganttua.core.context.GGContextConnector;
import com.gtech.garganttua.core.context.GGContextLock;
import com.gtech.garganttua.core.context.GGContextLockObject;
import com.gtech.garganttua.core.context.GGContextProcessor;
import com.gtech.garganttua.core.context.GGContextRoute;
import com.gtech.garganttua.core.context.GGContextSubscription;
import com.gtech.garganttua.core.spec.annotations.GGConnector;
import com.gtech.garganttua.core.spec.annotations.GGContextSource;
import com.gtech.garganttua.core.spec.annotations.GGDistributedLock;
import com.gtech.garganttua.core.spec.annotations.GGProcessor;
import com.gtech.garganttua.core.spec.enums.GGCoreEventCriticity;
import com.gtech.garganttua.core.spec.enums.GGCoreExecutionStage;
import com.gtech.garganttua.core.spec.exceptions.GGConnectorException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGConsumer;
import com.gtech.garganttua.core.spec.interfaces.IGGContextBuilder;
import com.gtech.garganttua.core.spec.interfaces.IGGContextEngine;
import com.gtech.garganttua.core.spec.interfaces.IGGContextSource;
import com.gtech.garganttua.core.spec.interfaces.IGGContextSourceConfigurationRegistry;
import com.gtech.garganttua.core.spec.interfaces.IGGCoreEventHandler;
import com.gtech.garganttua.core.spec.interfaces.IGGDistributedLock;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.interfaces.IGGProcessor;
import com.gtech.garganttua.core.spec.interfaces.IGGSubscription;
import com.gtech.garganttua.core.spec.objects.GGAssetContext;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGContextSourceConfiguration;
import com.gtech.garganttua.core.spec.objects.GGContextSourceConfigurationRegistry;
import com.gtech.garganttua.core.spec.objects.GGCoreEvent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GGContextEngine implements IGGContextEngine {

	private String assetId;
	private IGGContextBuilder contextBuilder;
	private String[] scanPackages;

	private Map<String, Map<String, Map<String, IGGConnector>>> connectors;
	private Map<String, Map<String, Map<String, IGGProcessor>>> processors;
	private Map<String, Map<String, Map<String, GGTopic>>> topics;
	private Map<String, Map<String, Map<String, GGDataflow>>> dataflows;
	private Map<String, Map<String, Map<String, GGSubscription>>> subscriptions;
	private Map<String, Map<String, Map<String, GGRoute>>> routes;
	private Map<String, Map<String, Map<String, IGGDistributedLock>>> locks;

	private HashMap<String, HashMap<String, Class<?>>> connectorClasses;
	private HashMap<String, HashMap<String, Class<?>>> processorsClasses;
	private HashMap<String, HashMap<String, Class<?>>> disctributedLocksClasses;
	
	private List<GGContextObjDescriptor> processorDescriptors;
	private List<GGContextObjDescriptor> connectorDescriptors;
	private List<GGContextObjDescriptor> lockDescriptors;
	
	@Getter
	private IGGObjectRegistryHub objectRegistries = new GGObjectRegistry();

	private GGCoreException initException;
	private IGGContextSourceConfigurationRegistry contextSourceConfigurationRegistry;

	@Getter
	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;
	
	private Date now;
	private String assetName;
	private String assetVersion;
	
	private List<IGGCoreEventHandler> eventsHandlers = new ArrayList<IGGCoreEventHandler>();
	
	@Override
	public void registerEventHandler(IGGCoreEventHandler handler) {
		this.eventsHandlers.add(handler);
	}

	@Override
	public void init(String assetId, IGGContextBuilder contextBuilder, String[] scanPackages, ExecutorService executorService, ScheduledExecutorService sExecutorService, String assetName, String assetVersion) {
		
		this.assetName = assetName;
		this.assetVersion = assetVersion;
		this.now = new Date();
		
		this.objectRegistries.addObjectRegistry("class", new GGObjectCreatorRegistry());
		
		log.info("==== GARGANTTUA FRAMEWORK ====");
		log.info("==== CONTEXT CONSTRUCTION ====");

		this.executorService = executorService;
		this.scheduledExecutorService = sExecutorService;
		this.initException = null;

		this.assetId = assetId;
		this.contextBuilder = contextBuilder;
		this.scanPackages = scanPackages;

		this.processorDescriptors = new ArrayList<GGContextObjDescriptor>();
		this.connectorDescriptors = new ArrayList<GGContextObjDescriptor>();
		this.lockDescriptors = new ArrayList<GGContextObjDescriptor>();
		
		this.connectors = new HashMap<String, Map<String, Map<String, IGGConnector>>>();
		this.processors = new HashMap<String, Map<String, Map<String, IGGProcessor>>>();
		this.topics = new HashMap<String, Map<String, Map<String, GGTopic>>>();
		this.dataflows = new HashMap<String, Map<String, Map<String, GGDataflow>>>();
		this.subscriptions = new HashMap<String, Map<String, Map<String, GGSubscription>>>();
		this.routes = new HashMap<String, Map<String, Map<String, GGRoute>>>();
		this.locks = new HashMap<String, Map<String, Map<String, IGGDistributedLock>>>();

		this.connectorClasses = new HashMap<String, HashMap<String, Class<?>>>();
		this.processorsClasses = new HashMap<String, HashMap<String, Class<?>>>();
		this.disctributedLocksClasses = new HashMap<String, HashMap<String, Class<?>>>();
		
		if (this.contextBuilder == null) {
			this.raiseEvent(new GGCoreEvent("No provided contextBuilder", GGCoreEventCriticity.FATAL, GGCoreExecutionStage.STARTUP, null));
		}
		
		try {
			this.createContext();
			this.findConnectors();
			this.findProcessors();
			this.findDistributedLocks();
		} catch (GGCoreException e) {
			log.error("Fatal error occured at startup", e);
			this.stop();
			this.raiseEvent(new GGCoreEvent(e.getMessage(), GGCoreEventCriticity.FATAL, GGCoreExecutionStage.STARTUP, e));
		}

		this.contextBuilder.getContext().forEach((tenantId, tenantContexts) -> {
			log.info("==== TENANT [" + tenantId + "] ====");

			this.connectors.put(tenantId, new HashMap<String, Map<String, IGGConnector>>());
			this.processors.put(tenantId, new HashMap<String, Map<String, IGGProcessor>>());
			this.topics.put(tenantId, new HashMap<String, Map<String, GGTopic>>());
			this.dataflows.put(tenantId, new HashMap<String, Map<String, GGDataflow>>());
			this.subscriptions.put(tenantId, new HashMap<String, Map<String, GGSubscription>>());
			this.routes.put(tenantId, new HashMap<String, Map<String, GGRoute>>());
			this.locks.put(tenantId, new HashMap<String, Map<String,IGGDistributedLock>>());

			tenantContexts.forEach((clusterId, context) -> {
				log.info("==== CLUSTER [" + clusterId + "] ====");

				this.connectors.get(tenantId).put(clusterId, new HashMap<String, IGGConnector>());
				this.processors.get(tenantId).put(clusterId, new HashMap<String, IGGProcessor>());
				this.topics.get(tenantId).put(clusterId, new HashMap<String, GGTopic>());
				this.dataflows.get(tenantId).put(clusterId, new HashMap<String, GGDataflow>());
				this.subscriptions.get(tenantId).put(clusterId, new HashMap<String, GGSubscription>());
				this.routes.get(tenantId).put(clusterId, new HashMap<String, GGRoute>());
				this.locks.get(tenantId).put(clusterId, new HashMap<String, IGGDistributedLock>());

				try {
					this.initConnectors(context, tenantId, clusterId);
					this.initDistributedLocks(context, tenantId, clusterId);
					this.initTopics(context, tenantId, clusterId);
					this.initDataflows(context, tenantId, clusterId);
					this.initSubscriptions(context, tenantId, clusterId);
					this.initProcessors(context, tenantId, clusterId);
					this.initRoutes(context, tenantId, clusterId);
				} catch (GGCoreException e) {
					this.initException = e;
				}

			});
		});
		if (this.initException != null) {
			log.error("Error during Garganttua Framework startup",this.initException);
			this.stop();
			this.raiseEvent(new GGCoreEvent(this.initException.getMessage(), GGCoreEventCriticity.FATAL, GGCoreExecutionStage.STARTUP, this.initException));
		}
	}

	private void findDistributedLocks() throws GGCoreException {
		log.info("==== FINDING DRISTRIBUTED LOCKS ====");
		if (this.scanPackages == null) {
			throw new GGCoreException("No provided scanning package");
		}

		for (String pack : this.scanPackages) {
			log.info(" -> Scanning package " + pack);
			Reflections reflections = new Reflections(pack);
			
			Set<Class<?>> locks__ = reflections.getTypesAnnotatedWith(GGDistributedLock.class);

			for (Class<?> clazz : locks__) {
				GGDistributedLock locksAnnotation = clazz.getAnnotation(GGDistributedLock.class);

				HashMap<String, Class<?>> versions = this.disctributedLocksClasses.get(locksAnnotation.type());
				
				if( versions == null ) {
					versions = new HashMap<String, Class<?>>();
					this.disctributedLocksClasses.put(locksAnnotation.type(), versions);
					
				}
				versions.put(locksAnnotation.version(), clazz);
				
				IGGDistributedLock lockObj = null;
				Constructor<?> ctor;
				try {
					ctor = clazz.getDeclaredConstructor();
				} catch (NoSuchMethodException | SecurityException e1) {
					throw new GGCoreException(e1);
				}
				try {
					lockObj = (IGGDistributedLock) ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new GGCoreException(e);
				}
				
				this.lockDescriptors.add(lockObj.getDescriptor());
				
				log.info("   -> Distributed Locks [" + locksAnnotation.type() + "]["+locksAnnotation.version()+"] registered");
			}
		}

		log.info(" -> Found " + this.lockDescriptors.size() + " locks");
	}

	private void findProcessors() throws GGCoreException {
		log.info("==== FINDING PROCESSORS ====");
		if (this.scanPackages == null) {
			throw new GGCoreException("No provided scanning package");
		}

		for (String pack : this.scanPackages) {
			log.info(" -> Scanning package " + pack);
			Reflections reflections = new Reflections(pack);
			Set<Class<?>> processors__ = reflections.getTypesAnnotatedWith(GGProcessor.class);

			for (Class<?> clazz : processors__) {
				GGProcessor processorsAnnotation = clazz.getAnnotation(GGProcessor.class);
				
				HashMap<String, Class<?>> versions = this.processorsClasses.get(processorsAnnotation.type());
				
				if( versions == null ) {
					versions = new HashMap<String, Class<?>>();
					this.processorsClasses.put(processorsAnnotation.type(), versions);
					
				}
				versions.put(processorsAnnotation.version(), clazz);
				
				IGGProcessor processorObj = null;
				Constructor<?> ctor;
				try {
					ctor = clazz.getDeclaredConstructor();
				} catch (NoSuchMethodException | SecurityException e1) {
					throw new GGCoreException(e1);
				}
				try {
					processorObj = (IGGProcessor) ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new GGCoreException(e);
				}
				
				this.processorDescriptors.add(processorObj.getDescriptor());
				log.info("   -> Processor [" + processorsAnnotation.type() + "]["+processorsAnnotation.version()+"] registered");
			}
		}

		log.info(" -> Found " + this.processorsClasses.size() + " processors");
	}

	private void findConnectors() throws GGCoreException {
		log.info("==== FINDING CONNECTORS ====");
		if (this.scanPackages == null) {
			throw new GGCoreException("No provided scanning package");
		}

		for (String pack : this.scanPackages) {
			log.info(" -> Scanning package " + pack);
			Reflections reflections = new Reflections(pack);
			Set<Class<?>> connectors__ = reflections.getTypesAnnotatedWith(GGConnector.class);

			for (Class<?> clazz : connectors__) {
				GGConnector connectorsAnnotation = clazz.getAnnotation(GGConnector.class);
				
				HashMap<String, Class<?>> versions = this.connectorClasses.get(connectorsAnnotation.type());
				
				if( versions == null ) {
					versions = new HashMap<String, Class<?>>();
					this.connectorClasses.put(connectorsAnnotation.type(), versions);
					
				}
				versions.put(connectorsAnnotation.version(), clazz);
				
				IGGConnector connectorObj = null;
				Constructor<?> ctor;
				try {
					ctor = clazz.getDeclaredConstructor();
				} catch (NoSuchMethodException | SecurityException e1) {
					throw new GGCoreException(e1);
				}
				try {
					connectorObj = (IGGConnector) ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new GGCoreException(e);
				}
				
				this.connectorDescriptors.add(connectorObj.getDescriptor());
				
				log.info("   -> Connector [" + connectorsAnnotation.type() + "]["+connectorsAnnotation.version()+"] registered");
			}
		}

		log.info(" -> Found " + this.connectorClasses.size() + " connectors");
	}

	private void initRoutes(GGContext context, String tenantId, String clusterId)
			throws GGCoreException, GGConnectorException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING ROUTES ====");

		for (GGContextRoute route : context.getRoutes()) {

			String from = route.getFrom();
			GGSubscription fromSubscription = this.subscriptions.get(tenantId).get(clusterId).get(from);
			if (fromSubscription == null) {
				throw new GGCoreException("Cannot construct route " + route.getUuid() + " : the subscription from "
						+ from + " is not registered");
			}

			String to = route.getTo();
			GGSubscription toSubscription = this.subscriptions.get(tenantId).get(clusterId).get(to);
			if (to != null && !to.isEmpty() && toSubscription == null) {
				throw new GGCoreException("Cannot construct route " + route.getUuid() + " : the subscription to "
						+ to + " is not registered");
			}
			
			GGSubscription exceptionSubscription = null;
			if( route.getExceptions() != null ) {
				String exception = route.getExceptions().getTo();
				exceptionSubscription = this.subscriptions.get(tenantId).get(clusterId).get(exception);
				if (exception != null && !exception.isEmpty() && exceptionSubscription == null) {
					throw new GGCoreException("Cannot construct route " + route.getUuid() + " : the exception subscription "
							+ exception + " is not registered");
				} 
				
				exceptionSubscription = new GGExceptionSubscription(exceptionSubscription, route.getExceptions());
				this.subscriptions.get(tenantId).get(clusterId).replace(exception, exceptionSubscription);
			}

			Map<Integer, IGGProcessor> processorsList = new HashMap<Integer, IGGProcessor>();
			for (Entry<Integer, GGContextProcessor> entry : route.getProcessors().entrySet()) {
				IGGProcessor proc = this.processors.get(tenantId).get(clusterId).get(entry.getValue().getUuid());
				processorsList.put(entry.getKey(), proc);
			}
			
			GGLockObject lock = null;
			
			if( route.getSynchronization() != null ) {
				GGContextLockObject ctxtLockObject = route.getSynchronization();
				
				IGGDistributedLock distributedLock = this.locks.get(tenantId).get(clusterId).get(ctxtLockObject.getLock());
				
				if( distributedLock != null ) {
					lock = new GGLockObject(distributedLock, ctxtLockObject);
				} else {
					throw new GGCoreException("Cannot construct route " + route.getUuid() + " : the distributed lock "
							+ ctxtLockObject.getLock() + " is not registered");
				}
				
			}

			GGRoute r = new GGRoute(fromSubscription, toSubscription, exceptionSubscription, lock, processorsList, route.getUuid(), clusterId, this.assetId);
			IGGConsumer consumer = fromSubscription.getConsumer();
			consumer.registerRoute(r);
			IGGConnector connector = fromSubscription.getConnector();
			connector.registerConsumer(fromSubscription.getSubscription(), r, tenantId, clusterId, this.assetId);
			
			if (toSubscription != null) {
				toSubscription.getConnector().registerProducer(toSubscription.getSubscription(), tenantId, clusterId, this.assetId);
			}
			
			if (exceptionSubscription != null) {
				exceptionSubscription.getConnector().registerProducer(exceptionSubscription.getSubscription(), tenantId, clusterId, this.assetId);
			}
			this.routes.get(tenantId).get(clusterId).put(r.getRouteUuid(), r);
			
			log.info("[" + tenantId + "][" + clusterId + "] -> Route " + route.getUuid() + " registered");

		}
	}

	private void initDataflows(GGContext context, String tenantId, String clusterId) throws GGCoreException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING DATAFLOWS ====");

		context.getDataflows().forEach(dataflow -> {
			log.info("[" + tenantId + "][" + clusterId + "] -> Dataflow " + dataflow.getUuid() + " "
					+ dataflow.getName() + " " + dataflow.getType() + " " + dataflow.getVersion()+ " registered");
			
			try {
				this.checkDataflowVersion(dataflow.getVersion());
				this.dataflows.get(tenantId).get(clusterId).put(dataflow.getUuid(), new GGDataflow(dataflow));
			} catch (GGCoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private void checkDataflowVersion(String version) throws GGCoreException {
		String[] splitted = version.split("\\.");
		
		if( splitted.length != 2 ) {
			throw new GGCoreException("The version "+version+" is incorrect, must be x.y format, with x and y integers");
		}
		try { 
			Integer.valueOf(splitted[0]);
			Integer.valueOf(splitted[1]);
		} catch( Exception e ) {
			throw new GGCoreException("The version "+version+" is incorrect, must be x.y format, with x and y integers");
		}
	}

	private void initSubscriptions(GGContext context, String tenantId, String clusterId) throws GGCoreException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING SUSBCRIPTIONS ====");

		for (GGContextSubscription subscription : context.getSubscriptions()) {
			String connectorName = subscription.getConnector();
			IGGConnector connector = this.connectors.get(tenantId).get(clusterId).get(connectorName);

			if (connector == null) {
				throw new GGCoreException("Cannot construct subscription " + subscription.getId()
						+ " : the connector " + connectorName + " is not registered");
			}

			String dataflowId = subscription.getDataFlow();
			GGDataflow dataflow = this.dataflows.get(tenantId).get(clusterId).get(dataflowId);

			if (dataflow == null) {
				throw new GGCoreException("Cannot construct subscription " + subscription.getId()
						+ " : the dataflow " + dataflowId + " is not registered");
			}

			String topicRef = subscription.getTopic();
			GGTopic topic = this.topics.get(tenantId).get(clusterId).get(topicRef);

			if (topic == null) {
				throw new GGCoreException("Cannot construct subscription " + subscription.getId() + " : the topic "
						+ topicRef + " is not registered");
			}

			GGSubscription s = new GGSubscription(dataflow, subscription, connector, topic, this.assetId, clusterId);

			if( this.subscriptions.get(tenantId).get(clusterId).get(subscription.getId()) != null ) {
				log.error("Cannot register subscription " + subscription.getId() + " : already registered");
				throw new GGCoreException("Cannot register subscription " + subscription.getId() + " : already registered");
			}
			
			this.subscriptions.get(tenantId).get(clusterId).put(subscription.getId(), s);

			log.info("[" + tenantId + "][" + clusterId + "] -> Subscription " + subscription.getId()
					+ " registered, publicationMode=" + subscription.getPublicationMode());
		}
	}

	private void initTopics(GGContext context, String tenantId, String clusterId) throws GGCoreException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING TOPICS ====");

		context.getTopics().forEach(topic -> {
			log.info("[" + tenantId + "][" + clusterId + "] -> Topic " + topic.getRef() + " registered");
			this.topics.get(tenantId).get(clusterId).put(topic.getRef(), new GGTopic(topic));
		});
	}

	private void initProcessors(GGContext context, String tenantId, String clusterId) throws GGCoreException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING PROCESSORS ====");

		Map<String, GGContextProcessor> ctxtProcessors = this.contextBuilder.getProcessors(tenantId, clusterId);

		log.info("[" + tenantId + "][" + clusterId + "] -> Starting processors configuration");

		for (Entry<String, GGContextProcessor> ctxtProcessor : ctxtProcessors.entrySet()) {

			log.info("[" + tenantId + "][" + clusterId + "]   -> Configuring " + ctxtProcessor.getKey() + " : "
					+ ctxtProcessor.getValue().getType());

			Class<?> processor = null;
			
			try {
				processor = this.processorsClasses.get(ctxtProcessor.getValue().getType()).get(ctxtProcessor.getValue().getVersion());
			} catch(Exception e) {
				throw new GGCoreException(
						"Cannot find processor of type " + ctxtProcessor.getValue().getType() + " and version "+ctxtProcessor.getValue().getVersion()+".");
			}
			if (processor == null) {
				throw new GGCoreException(
						"Cannot find processor of type " + ctxtProcessor.getValue().getType() + " and version "+ctxtProcessor.getValue().getVersion()+".");
			}

			if (!IGGProcessor.class.isAssignableFrom(processor)) {
				throw new GGCoreException(
						"The class [" + processor.getName() + "] must implements the IGGProcessor interface.");
			}

			IGGProcessor processorObj = null;
			Constructor<?> ctor;
			try {
				ctor = processor.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGCoreException(e1);
			}
			try {
				processorObj = (IGGProcessor) ctor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGCoreException(e);
			}
			processorObj.setType("IGGProcessor::" + ctxtProcessor.getValue().getType());
			processorObj.setContextEngine(this);
			processorObj.setConfiguration(ctxtProcessor.getValue().getConfiguration(), tenantId, clusterId, this.assetId, this.objectRegistries);

			this.processors.get(tenantId).get(clusterId).put(ctxtProcessor.getKey(), processorObj);

			log.info("[" + tenantId + "][" + clusterId + "]   -> Processor registered : " + ctxtProcessor.getKey()
					+ " of type " + ctxtProcessor.getValue().getType()+" and version "+ctxtProcessor.getValue().getVersion());
		}
	}
	
	private void initDistributedLocks(GGContext context, String tenantId, String clusterId) throws GGCoreException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING DISTRIBUTED LOCKS ====");

		List<GGContextLock> ctxtLocks = context.getDistributedLocks();
		List<GGContextLock> toBeDeleted = new ArrayList<GGContextLock>();
		
		if( ctxtLocks == null ) {
			log.info("[" + tenantId + "][" + clusterId + "]  -> No distributed locks to configure");
			return;
		}
		log.info("[" + tenantId + "][" + clusterId + "]  -> Starting distributed locks configuration");

		for (GGContextLock ctxtLock : ctxtLocks) {
			log.info("[" + tenantId + "][" + clusterId + "]   -> Configuring " + ctxtLock.getType() + " : "
					+ ctxtLock.getName());

			IGGDistributedLock alreadyExist = this.locks.get(tenantId).get(clusterId).get(ctxtLock.getName());
			String config = ctxtLock.getConfiguration();
			
			if( config == null ) {
				config = "";
				ctxtLock.setConfiguration("");
			}
			
			if (alreadyExist != null ) {
				String aeConfig = alreadyExist.getConfiguration();
				if( aeConfig == null ) {
					alreadyExist.setConfiguration("", tenantId, clusterId, this.assetId, this.objectRegistries);
					aeConfig = "";
				}
				
				if( !config.equals(aeConfig) ) {
					
					throw new GGCoreException("Another DistributedLock with name " + ctxtLock.getName() + " and type "
							+ ctxtLock.getType() + " is already registered with a different configuration : ["
							+ config + "] vs [" + aeConfig + "]");
				} else {
					log.info("[" + tenantId + "][" + clusterId + "]   -> DistributedLock already registered : "
							+ ctxtLock.getName() + " of type " + ctxtLock.getType());
					toBeDeleted.add(ctxtLock);
					continue;
				}
			}
			Class<?> lock = null;
			try {
				lock = this.disctributedLocksClasses.get(ctxtLock.getType()).get(ctxtLock.getVersion());
			} catch (Exception e) {
				throw new GGCoreException("Cannot find DistribuedLock of type " + ctxtLock.getType() + " and version "+ctxtLock.getVersion()+".");
			}
			if (lock == null) {
				throw new GGCoreException("Cannot find DistribuedLock of type " + ctxtLock.getType() + " and version "+ctxtLock.getVersion()+".");
			}

			if (!IGGDistributedLock.class.isAssignableFrom(lock)) {
				throw new GGCoreException(
						"The class [" + lock.getName() + "] must implements the IGGDistributedLock interface.");
			}

			IGGDistributedLock distributedLockObj = null;
			Constructor<?> ctor;
			try {
				ctor = lock.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGCoreException(e1);
			}
			try {
				distributedLockObj = (IGGDistributedLock) ctor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGCoreException(e);
			}
			
			distributedLockObj.setConfiguration(ctxtLock.getConfiguration()==null?"":ctxtLock.getConfiguration(), tenantId, clusterId, this.assetId, this.objectRegistries);

			this.locks.get(tenantId).get(clusterId).put(ctxtLock.getName(), distributedLockObj);

			log.info("[" + tenantId + "][" + clusterId + "]   -> Distributed Lock registered : " + ctxtLock.getName()
					+ " of type " + ctxtLock.getType()+" and version "+ctxtLock.getVersion());
		}
		ctxtLocks.removeAll(toBeDeleted);
	}

	private void initConnectors(GGContext context, String tenantId, String clusterId) throws GGCoreException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING CONNECTORS ====");

		List<GGContextConnector> ctxtConnectors = context.getConnectors();
		
		List<GGContextConnector> toBeDeleted = new ArrayList<GGContextConnector>();

		log.info("[" + tenantId + "][" + clusterId + "]  -> Starting connector configuration");

		for (GGContextConnector ctxtConnector : ctxtConnectors) {
			log.info("[" + tenantId + "][" + clusterId + "]   -> Configuring " + ctxtConnector.getType() + " : "
					+ ctxtConnector.getName());

			IGGConnector alreadyExist = this.connectors.get(tenantId).get(clusterId).get(ctxtConnector.getName());
			String config = ctxtConnector.getConfiguration();
			
			if( config == null ) {
				config = "";
				ctxtConnector.setConfiguration("");
			}
			
			if (alreadyExist != null ) {
				String aeConfig = alreadyExist.getConfiguration();
				if( aeConfig == null ) {
					alreadyExist.setConfiguration("", tenantId, clusterId, this.assetId, this.objectRegistries);
					aeConfig = "";
				}
				
				if( !config.equals(aeConfig) ) {
					
					throw new GGCoreException("Another Connector with name " + ctxtConnector.getName() + " and type "
							+ alreadyExist.getType() + " is already registered with a different configuration : ["
							+ config + "] vs [" + aeConfig + "]");
				} else {
					log.info("[" + tenantId + "][" + clusterId + "]   -> Connector already registered : "
							+ ctxtConnector.getName() + " of type " + ctxtConnector.getType());
					toBeDeleted.add(ctxtConnector);
					continue;
				}
			}
			Class<?> connector = null;
			try {
				connector = this.connectorClasses.get(ctxtConnector.getType()).get(ctxtConnector.getVersion());
			} catch (Exception e) {
				throw new GGCoreException("Cannot find connector of type " + ctxtConnector.getType() + " and version "+ctxtConnector.getVersion()+".");
			}
			if (connector == null) {
				throw new GGCoreException("Cannot find connector of type " + ctxtConnector.getType() + " and version "+ctxtConnector.getVersion()+".");
			}

			if (!IGGConnector.class.isAssignableFrom(connector)) {
				throw new GGCoreException(
						"The class [" + connector.getName() + "] must implements the IGGConnector interface.");
			}

			IGGConnector connectorObj = null;
			Constructor<?> ctor;
			try {
				ctor = connector.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGCoreException(e1);
			}
			try {
				connectorObj = (IGGConnector) ctor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGCoreException(e);
			}
			connectorObj.setPoolExecutor(this.executorService);
			connectorObj.setConfiguration(ctxtConnector.getConfiguration()==null?"":ctxtConnector.getConfiguration(), tenantId, clusterId, this.assetId, this.objectRegistries);
			connectorObj.setName(ctxtConnector.getName());
			
			if( connectorObj instanceof GGCoreEventsConnector) {
				this.registerEventHandler((GGCoreEventsConnector)connectorObj);
			}
			
			this.connectors.get(tenantId).get(clusterId).put(ctxtConnector.getName(), connectorObj);

			log.info("[" + tenantId + "][" + clusterId + "]   -> Connector registered : " + ctxtConnector.getName()
					+ " of type " + ctxtConnector.getType()+" and version "+ctxtConnector.getVersion());
		}
		
		ctxtConnectors.removeAll(toBeDeleted);
		
	}

	private void createContext() throws GGCoreException {
		log.info("==== GETTING CONTEXTS ====");
		List<Class<?>> contextSources = new ArrayList<Class<?>>();
		if (this.scanPackages == null) {
			throw new GGCoreException("No provided scanning package");
		}

		for (String pack : this.scanPackages) {
			log.info(" -> Scanning package " + pack);
//			Reflections reflections = new Reflections(pack, GGContextEngine.class.getClassLoader());
			Reflections reflections = new Reflections(pack);
			Set<Class<?>> sources = reflections.getTypesAnnotatedWith(GGContextSource.class);

			contextSources.addAll(sources);
		}

		log.info(" -> Found " + contextSources.size() + " context sources");
		for (Class<?> clazz : contextSources) {

			if (!IGGContextSource.class.isAssignableFrom(clazz)) {
				throw new GGCoreException(
						"The class [" + clazz.getName() + "] must implements the IGGContextSource interface.");
			}

			GGContextSource contextSourceAnnotation = clazz.getAnnotation(GGContextSource.class);

			Constructor<?> toto;
			try {
				toto = clazz.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGCoreException(e1);
			}
			IGGContextSource contextSource = null;
			try {
				contextSource = (IGGContextSource) toto.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGCoreException(e);
			}

			log.info("   -> Context source registered : " + contextSourceAnnotation.name());

			String[] configurations = this.contextSourceConfigurationRegistry.getContextSourceConfiguration(contextSourceAnnotation.name());

			if (configurations == null) {
				throw new GGCoreException(
						"No configuration found for context source " + contextSourceAnnotation.name());
			}

			contextSource.init(this.assetId, configurations);

			log.info("   -> Getting context from " + contextSourceAnnotation.name());
			List<GGContext> contexts = contextSource.getContexts(this.now);

			for (GGContext context : contexts) {
				if (context.getTenantId() == null || context.getTenantId().isEmpty()) {
					throw new GGCoreException("Invalid context : no provided tenantId");
				}
				if (context.getClusterId() == null || context.getClusterId().isEmpty()) {
					throw new GGCoreException("Invalid context : no provided tenantId");
				}
				this.contextBuilder.addContext(context);
			}
		}
	}

	@Override
	public void start() {
		log.info("==== APPLYING CONFIGURATION ====");
		this.processors.forEach((tenantId, clusters) -> {
			clusters.forEach((clusterId, processors) -> {
				processors.forEach((type, processor) -> {
					log.info("[" + tenantId + "][" + clusterId + "] Configuring processor " + type);
					try {
						processor.applyConfiguration();
					} catch (Exception e) {
						log.error("Unable to start Garganttua Framework", e);
						this.stop();
						this.raiseEvent(new GGCoreEvent("Unable to start Garganttua Framework", GGCoreEventCriticity.FATAL, GGCoreExecutionStage.STARTUP, new GGCoreException(e)));
					}
					log.debug("[" + tenantId + "][" + clusterId + "] Configured " + type);
				});
			});
		});
		this.connectors.forEach((tenantId, clusters) -> {
			clusters.forEach((clusterId, connectors) -> {
				connectors.forEach((type, connector) -> {
					log.info("[" + tenantId + "][" + clusterId + "] Configuring connector " + type);
					try {
						connector.applyConfiguration();
					} catch (Exception e) {
						log.error("Unable to start Garganttua Framework", e);
						this.stop();
						this.raiseEvent(new GGCoreEvent("Unable to start Garganttua Framework", GGCoreEventCriticity.FATAL, GGCoreExecutionStage.STARTUP, new GGCoreException(e)));
					}
					log.debug("[" + tenantId + "][" + clusterId + "] Configured " + type);
				});
			});
		});
		
		log.info("==== STARTING CONNECTORS ====");

		this.connectors.forEach((tenantId, clusters) -> {
			clusters.forEach((clusterId, connectors) -> {
				connectors.forEach((type, connector) -> {
					log.info("[" + tenantId + "][" + clusterId + "] Starting connector " + type);
					try {
						connector.start();
					} catch (Exception e) {
						log.error("Unable to start Garganttua Framework", e);
						this.stop();
						this.raiseEvent(new GGCoreEvent("Unable to start Garganttua Framework", GGCoreEventCriticity.FATAL, GGCoreExecutionStage.STARTUP, new GGCoreException(e)));
					}
					log.debug("[" + tenantId + "][" + clusterId + "] Started connector " + type);
				});
			});
		});
		
		log.info("==== STARTING ROUTES ====");
		
		this.routes.forEach((tenantId, clusters) -> {
			clusters.forEach((clusterId, routes) -> {
				routes.forEach((routeId, route) -> {
					log.info("[" + tenantId + "][" + clusterId + "] Starting route " + routeId);
					try {
						route.start(this.scheduledExecutorService);
					} catch (Exception e) {
						log.error("Unable to start Garganttua Framework", e);
						this.stop();
						this.raiseEvent(new GGCoreEvent("Unable to start Garganttua Framework", GGCoreEventCriticity.FATAL, GGCoreExecutionStage.STARTUP, new GGCoreException(e)));
					}
					log.debug("[" + tenantId + "][" + clusterId + "] Started route " + routeId);
				});
			});
		});
		this.raiseEvent(new GGCoreEvent("Garganttua Core Context Engine started", GGCoreEventCriticity.INFO, GGCoreExecutionStage.STARTUP, null));
	}

	/**
	 * 
	 * @param GGCoreEvent
	 */
	private void raiseEvent(GGCoreEvent GGCoreEvent) {
		this.executorService.execute(new Thread() {
			@Override
			public void run() {
				eventsHandlers.forEach(eh -> {
					executorService.execute(new Thread() {
						@Override
						public void run() {
							eh.handleEvent(GGCoreEvent);
						}
					});
				});
			}
		});
	}

	@Override
	public void stop() {
		log.info("==== STOPPING CONNECTORS ====");

		this.connectors.forEach((tenantId, clusters) -> {
			clusters.forEach((clusterId, connectors) -> {
				connectors.forEach((type, connector) -> {
					log.info("[" + tenantId + "][" + clusterId + "] Stopping " + type);
					try {
						connector.stop();
					} catch (Exception e) {
						log.error("Unable to stop Garganttua Framework", e);
						this.raiseEvent(new GGCoreEvent("Unable to stop Garganttua Framework", GGCoreEventCriticity.FATAL, GGCoreExecutionStage.SHUTDOWN, new GGCoreException(e)));
					}
					log.debug("[" + tenantId + "][" + clusterId + "] Stopped " + type);
				});
			});
		});
		
		log.info("==== STOPPING ROUTES ====");
		
		this.routes.forEach((tenantId, clusters) -> {
			clusters.forEach((clusterId, routes) -> {
				routes.forEach((routeId, route) -> {
					log.info("[" + tenantId + "][" + clusterId + "] Stopping route " + routeId);
					try {
						route.stop();
					} catch (Exception e) {
						log.error("Unable to stop Garganttua Framework", e);
						this.raiseEvent(new GGCoreEvent("Unable to stop Garganttua Framework", GGCoreEventCriticity.FATAL, GGCoreExecutionStage.SHUTDOWN, new GGCoreException(e)));
					}
					log.debug("[" + tenantId + "][" + clusterId + "] Stopped route " + routeId);
				});
			});
		});
	}

	@Override
	public void reloadContext() {
		this.stop();
		this.init(this.assetId, this.contextBuilder, this.scanPackages, this.executorService, this.scheduledExecutorService, this.assetName, this.assetVersion);
		this.start();
	}

	@Override
	public List<IGGConnector> getConnectors() {
		ArrayList<IGGConnector> c = new ArrayList<IGGConnector>();
		this.connectors.forEach((tenantId, clusters) -> {
			clusters.forEach((clusterId, connectors) -> {
				connectors.forEach((type, connector) -> {
					c.add(connector);
				});
			});
		});

		return c;
	}

	@Override
	public void registerContextSourceConfiguratorRegistry(IGGContextSourceConfigurationRegistry contextSourceConfigurationRegistry) {
		this.contextSourceConfigurationRegistry = contextSourceConfigurationRegistry;
	}

	public IGGConnector getConnector(String tenantId, String clusterId, String name) {
		return this.connectors.get(tenantId).get(clusterId).get(name);
	}

	@Override
	public IGGSubscription getSubscription(String subscriptionId, String tenantId, String clusterId) {
		return this.subscriptions.get(tenantId).get(clusterId).get(subscriptionId);
	}

	@Override
	public GGAssetContext getAssetContext() {
		return new GGAssetContext(this.assetId, this.now, this.assetName, this.assetVersion, this.processorDescriptors, this.connectorDescriptors, this.lockDescriptors, this.contextBuilder.getContext());
	}
	
	public static void main(String[] args) {
		int threadPoolSize = 100;
		int maxThreadPoolSize = 200;
		long threadPoolKeepAliveTime = 100;
		TimeUnit threadPoolKeepAliveTimeUnit = TimeUnit.SECONDS;
		BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
		ExecutorService executorService = new ThreadPoolExecutor(threadPoolSize/2, maxThreadPoolSize/2, threadPoolKeepAliveTime, threadPoolKeepAliveTimeUnit, workQueue);
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(threadPoolSize/2);;
		IGGContextBuilder contextBuilder = new GGContextBuilder();
		IGGContextSourceConfigurationRegistry configRegistry = new GGContextSourceConfigurationRegistry();
		String[] contextFiles = new String[1];
		contextFiles[0] = args[0];
		String[] pack = {"com.gtech"};
		configRegistry.registerContextSourceConfiguration(new GGContextSourceConfiguration("GGContextFileSource", contextFiles ));

		GGContextEngine engine = new GGContextEngine();
		engine.registerContextSourceConfiguratorRegistry(configRegistry);

		engine.init(args[1], contextBuilder, pack, executorService, scheduledExecutorService, args[2], args[3]);
	}
	
}
