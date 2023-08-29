/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.connectors.core.events.GGEventsEventsConnector;
import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.context.GGEventsContextLock;
import com.garganttua.events.context.GGEventsContextProcessor;
import com.garganttua.events.context.GGEventsContextRoute;
import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.spec.enums.GGEventsEventCriticity;
import com.garganttua.events.spec.enums.GGEventsExecutionStage;
import com.garganttua.events.spec.exceptions.GGEventsConnectorException;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsContextBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsContextSourceConfigurationRegistry;
import com.garganttua.events.spec.interfaces.IGGEventsDistributedLock;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.IGGEventsSubscription;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConnector;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextLockObject;
import com.garganttua.events.spec.objects.GGEventsAssetContext;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsEvent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GGEventsEngine__legacy implements IGGEventsEngine {

	private String assetId;
	private IGGEventsContextBuilder contextBuilder;
	private String[] scanPackages;

	private Map<String, Map<String, Map<String, IGGEventsConnector>>> connectors;
	private Map<String, Map<String, Map<String, IGGEventsProcessor>>> processors;
	private Map<String, Map<String, Map<String, GGEventsTopic>>> topics;
	private Map<String, Map<String, Map<String, GGEventsDataflow>>> dataflows;
	private Map<String, Map<String, Map<String, GGEventsSubscription>>> subscriptions;
	private Map<String, Map<String, Map<String, GGEventsRoute>>> routes;
	private Map<String, Map<String, Map<String, IGGEventsDistributedLock>>> locks;

	private HashMap<String, HashMap<String, Class<?>>> connectorClasses;
	private HashMap<String, HashMap<String, Class<?>>> processorsClasses;
	private HashMap<String, HashMap<String, Class<?>>> disctributedLocksClasses;
	
	private List<GGEventsContextObjDescriptor> processorDescriptors;
	private List<GGEventsContextObjDescriptor> connectorDescriptors;
	private List<GGEventsContextObjDescriptor> lockDescriptors;
	
	@Getter
	private IGGEventsObjectRegistryHub objectRegistries = new GGEventsObjectRegistry();

	private GGEventsException initException;
	private IGGEventsContextSourceConfigurationRegistry contextSourceConfigurationRegistry;

	@Getter
	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;
	
	private Date now;
	private String assetName;
	private String assetVersion;
	
	private List<IGGEventsEventHandler> eventsHandlers = new ArrayList<IGGEventsEventHandler>();
	
	@Override
	public void registerEventHandler(IGGEventsEventHandler handler) {
		this.eventsHandlers.add(handler);
	}

	@Override
	public void init(String assetId, IGGEventsContextBuilder contextBuilder, String[] scanPackages, ExecutorService executorService, ScheduledExecutorService sExecutorService, String assetName, String assetVersion) {
		
		this.assetName = assetName;
		this.assetVersion = assetVersion;
		this.now = new Date();
		
		this.objectRegistries.addObjectRegistry(GGEventsObjectCreatorRegistry.LABEL, new GGEventsObjectCreatorRegistry());
		
		log.info("============================================");
		log.info("======                                ======");
		log.info("====== Starting Garganttua Events     ======");
		log.info("======                                ======");
		log.info("============================================");
		log.info("==== CONTEXT CONSTRUCTION ====");

		this.executorService = executorService;
		this.scheduledExecutorService = sExecutorService;
		this.initException = null;

		this.assetId = assetId;
		this.contextBuilder = contextBuilder;
		this.scanPackages = scanPackages;

		this.processorDescriptors = new ArrayList<GGEventsContextObjDescriptor>();
		this.connectorDescriptors = new ArrayList<GGEventsContextObjDescriptor>();
		this.lockDescriptors = new ArrayList<GGEventsContextObjDescriptor>();
		
		this.connectors = new HashMap<String, Map<String, Map<String, IGGEventsConnector>>>();
		this.processors = new HashMap<String, Map<String, Map<String, IGGEventsProcessor>>>();
		this.topics = new HashMap<String, Map<String, Map<String, GGEventsTopic>>>();
		this.dataflows = new HashMap<String, Map<String, Map<String, GGEventsDataflow>>>();
		this.subscriptions = new HashMap<String, Map<String, Map<String, GGEventsSubscription>>>();
		this.routes = new HashMap<String, Map<String, Map<String, GGEventsRoute>>>();
		this.locks = new HashMap<String, Map<String, Map<String, IGGEventsDistributedLock>>>();

		this.connectorClasses = new HashMap<String, HashMap<String, Class<?>>>();
		this.processorsClasses = new HashMap<String, HashMap<String, Class<?>>>();
		this.disctributedLocksClasses = new HashMap<String, HashMap<String, Class<?>>>();
		
		if (this.contextBuilder == null) {
			this.raiseEvent(new GGEventsEvent("No provided contextBuilder", GGEventsEventCriticity.FATAL, GGEventsExecutionStage.STARTUP, null));
		}
		
		try {
			this.createContext();
			this.findConnectors();
			this.findProcessors();
			this.findDistributedLocks();
		} catch (GGEventsException e) {
			log.error("Fatal error occured at startup", e);
			this.stop();
			this.raiseEvent(new GGEventsEvent(e.getMessage(), GGEventsEventCriticity.FATAL, GGEventsExecutionStage.STARTUP, e));
		}

		this.contextBuilder.getContext().forEach((tenantId, tenantContexts) -> {
			log.info("==== TENANT [" + tenantId + "] ====");

			this.connectors.put(tenantId, new HashMap<String, Map<String, IGGEventsConnector>>());
			this.processors.put(tenantId, new HashMap<String, Map<String, IGGEventsProcessor>>());
			this.topics.put(tenantId, new HashMap<String, Map<String, GGEventsTopic>>());
			this.dataflows.put(tenantId, new HashMap<String, Map<String, GGEventsDataflow>>());
			this.subscriptions.put(tenantId, new HashMap<String, Map<String, GGEventsSubscription>>());
			this.routes.put(tenantId, new HashMap<String, Map<String, GGEventsRoute>>());
			this.locks.put(tenantId, new HashMap<String, Map<String,IGGEventsDistributedLock>>());

			tenantContexts.forEach((clusterId, context) -> {
				log.info("==== CLUSTER [" + clusterId + "] ====");

				this.connectors.get(tenantId).put(clusterId, new HashMap<String, IGGEventsConnector>());
				this.processors.get(tenantId).put(clusterId, new HashMap<String, IGGEventsProcessor>());
				this.topics.get(tenantId).put(clusterId, new HashMap<String, GGEventsTopic>());
				this.dataflows.get(tenantId).put(clusterId, new HashMap<String, GGEventsDataflow>());
				this.subscriptions.get(tenantId).put(clusterId, new HashMap<String, GGEventsSubscription>());
				this.routes.get(tenantId).put(clusterId, new HashMap<String, GGEventsRoute>());
				this.locks.get(tenantId).put(clusterId, new HashMap<String, IGGEventsDistributedLock>());

				try {
					this.initConnectors(context, tenantId, clusterId);
					this.initDistributedLocks(context, tenantId, clusterId);
					this.initTopics(context, tenantId, clusterId);
					this.initDataflows(context, tenantId, clusterId);
					this.initSubscriptions(context, tenantId, clusterId);
					this.initProcessors(context, tenantId, clusterId);
					this.initRoutes(context, tenantId, clusterId);
				} catch (GGEventsException e) {
					this.initException = e;
				}

			});
		});
		if (this.initException != null) {
			log.error("Error during Garganttua Framework startup",this.initException);
			this.stop();
			this.raiseEvent(new GGEventsEvent(this.initException.getMessage(), GGEventsEventCriticity.FATAL, GGEventsExecutionStage.STARTUP, this.initException));
		}
	}

	

	private void initRoutes(GGEventsContext context, String tenantId, String clusterId)
			throws GGEventsException, GGEventsConnectorException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING ROUTES ====");

		for (GGEventsContextRoute route : context.getRoutes()) {

			String from = route.getFrom();
			GGEventsSubscription fromSubscription = this.subscriptions.get(tenantId).get(clusterId).get(from);
			if (fromSubscription == null) {
				throw new GGEventsException("Cannot construct route " + route.getUuid() + " : the subscription from "
						+ from + " is not registered");
			}

			String to = route.getTo();
			GGEventsSubscription toSubscription = this.subscriptions.get(tenantId).get(clusterId).get(to);
			if (to != null && !to.isEmpty() && toSubscription == null) {
				throw new GGEventsException("Cannot construct route " + route.getUuid() + " : the subscription to "
						+ to + " is not registered");
			}
			
			GGEventsSubscription exceptionSubscription = null;
			if( route.getExceptions() != null ) {
				String exception = route.getExceptions().getTo();
				exceptionSubscription = this.subscriptions.get(tenantId).get(clusterId).get(exception);
				if (exception != null && !exception.isEmpty() && exceptionSubscription == null) {
					throw new GGEventsException("Cannot construct route " + route.getUuid() + " : the exception subscription "
							+ exception + " is not registered");
				} 
				
				exceptionSubscription = new GGEventsExceptionSubscription(exceptionSubscription, route.getExceptions());
				this.subscriptions.get(tenantId).get(clusterId).replace(exception, exceptionSubscription);
			}

			Map<Integer, IGGEventsProcessor> processorsList = new HashMap<Integer, IGGEventsProcessor>();
			for (Entry<Integer, GGEventsContextProcessor> entry : route.getProcessors().entrySet()) {
				IGGEventsProcessor proc = this.processors.get(tenantId).get(clusterId).get(entry.getValue().getUuid());
				processorsList.put(entry.getKey(), proc);
			}
			
			GGEventsLockObject lock = null;
			
			if( route.getSynchronization() != null ) {
				IGGEventsContextLockObject ctxtLockObject = route.getSynchronization();
				
				IGGEventsDistributedLock distributedLock = this.locks.get(tenantId).get(clusterId).get(ctxtLockObject.getLock());
				
				if( distributedLock != null ) {
					lock = new GGEventsLockObject(distributedLock, ctxtLockObject);
				} else {
					throw new GGEventsException("Cannot construct route " + route.getUuid() + " : the distributed lock "
							+ ctxtLockObject.getLock() + " is not registered");
				}
				
			}

			GGEventsRoute r = new GGEventsRoute(fromSubscription, toSubscription, exceptionSubscription, lock, processorsList, route.getUuid(), tenantId, clusterId, this.assetId);
			
			this.routes.get(tenantId).get(clusterId).put(r.getRouteUuid(), r);
			
			log.info("[" + tenantId + "][" + clusterId + "] -> Route " + route.getUuid() + " registered");

		}
	}

	private void initDataflows(GGEventsContext context, String tenantId, String clusterId) throws GGEventsException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING DATAFLOWS ====");

		context.getDataflows().forEach(dataflow -> {
			log.info("[" + tenantId + "][" + clusterId + "] -> Dataflow " + dataflow.getUuid() + " "
					+ dataflow.getName() + " " + dataflow.getType() + " " + dataflow.getVersion()+ " registered");
			
			try {
				this.checkDataflowVersion(dataflow.getVersion());
				this.dataflows.get(tenantId).get(clusterId).put(dataflow.getUuid(), new GGEventsDataflow(dataflow));
			} catch (GGEventsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	private void checkDataflowVersion(String version) throws GGEventsException {
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

	private void initSubscriptions(GGEventsContext context, String tenantId, String clusterId) throws GGEventsException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING SUSBCRIPTIONS ====");

		for (GGEventsContextSubscription subscription : context.getSubscriptions()) {
			String connectorName = subscription.getConnector();
			IGGEventsConnector connector = this.connectors.get(tenantId).get(clusterId).get(connectorName);

			if (connector == null) {
				throw new GGEventsException("Cannot construct subscription " + subscription.getId()
						+ " : the connector " + connectorName + " is not registered");
			}

			String dataflowId = subscription.getDataFlow();
			GGEventsDataflow dataflow = this.dataflows.get(tenantId).get(clusterId).get(dataflowId);

			if (dataflow == null) {
				throw new GGEventsException("Cannot construct subscription " + subscription.getId()
						+ " : the dataflow " + dataflowId + " is not registered");
			}

			String topicRef = subscription.getTopic();
			GGEventsTopic topic = this.topics.get(tenantId).get(clusterId).get(topicRef);

			if (topic == null) {
				throw new GGEventsException("Cannot construct subscription " + subscription.getId() + " : the topic "
						+ topicRef + " is not registered");
			}

			GGEventsSubscription s = new GGEventsSubscription(dataflow, subscription, connector, topic, this.assetId, clusterId);

			if( this.subscriptions.get(tenantId).get(clusterId).get(subscription.getId()) != null ) {
				log.error("Cannot register subscription " + subscription.getId() + " : already registered");
				throw new GGEventsException("Cannot register subscription " + subscription.getId() + " : already registered");
			}
			
			this.subscriptions.get(tenantId).get(clusterId).put(subscription.getId(), s);

			log.info("[" + tenantId + "][" + clusterId + "] -> Subscription " + subscription.getId()
					+ " registered, publicationMode=" + subscription.getPublicationMode());
		}
	}

	private void initTopics(GGEventsContext context, String tenantId, String clusterId) throws GGEventsException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING TOPICS ====");

		context.getTopics().forEach(topic -> {
			log.info("[" + tenantId + "][" + clusterId + "] -> Topic " + topic.getRef() + " registered");
			this.topics.get(tenantId).get(clusterId).put(topic.getRef(), new GGEventsTopic(topic));
		});
	}

	private void initProcessors(GGEventsContext context, String tenantId, String clusterId) throws GGEventsException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING PROCESSORS ====");

		Map<String, GGEventsContextProcessor> ctxtProcessors = this.contextBuilder.getProcessors(tenantId, clusterId);

		log.info("[" + tenantId + "][" + clusterId + "] -> Starting processors configuration");

		for (Entry<String, GGEventsContextProcessor> ctxtProcessor : ctxtProcessors.entrySet()) {

			log.info("[" + tenantId + "][" + clusterId + "]   -> Configuring " + ctxtProcessor.getKey() + " : "
					+ ctxtProcessor.getValue().getType());

			Class<?> processor = null;
			
			try {
				processor = this.processorsClasses.get(ctxtProcessor.getValue().getType()).get(ctxtProcessor.getValue().getVersion());
			} catch(Exception e) {
				throw new GGEventsException(
						"Cannot find processor of type " + ctxtProcessor.getValue().getType() + " and version "+ctxtProcessor.getValue().getVersion()+".");
			}
			if (processor == null) {
				throw new GGEventsException(
						"Cannot find processor of type " + ctxtProcessor.getValue().getType() + " and version "+ctxtProcessor.getValue().getVersion()+".");
			}

			if (!IGGEventsProcessor.class.isAssignableFrom(processor)) {
				throw new GGEventsException(
						"The class [" + processor.getName() + "] must implements the IGGEventsProcessor interface.");
			}

			IGGEventsProcessor processorObj = null;
			Constructor<?> ctor;
			try {
				ctor = processor.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGEventsException(e1);
			}
			try {
				processorObj = (IGGEventsProcessor) ctor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGEventsException(e);
			}
			processorObj.setType("IGGEventsProcessor::" + ctxtProcessor.getValue().getType());
			processorObj.setContextEngine(this);
			processorObj.setConfiguration(ctxtProcessor.getValue().getConfiguration(), tenantId, clusterId, this.assetId, this.objectRegistries);

			this.processors.get(tenantId).get(clusterId).put(ctxtProcessor.getKey(), processorObj);

			log.info("[" + tenantId + "][" + clusterId + "]   -> Processor registered : " + ctxtProcessor.getKey()
					+ " of type " + ctxtProcessor.getValue().getType()+" and version "+ctxtProcessor.getValue().getVersion());
		}
	}
	
	private void initDistributedLocks(GGEventsContext context, String tenantId, String clusterId) throws GGEventsException {
		log.info("[" + tenantId + "][" + clusterId + "] ==== CREATING DISTRIBUTED LOCKS ====");

		List<GGEventsContextLock> ctxtLocks = context.getDistributedLocks();
		List<GGEventsContextLock> toBeDeleted = new ArrayList<GGEventsContextLock>();
		
		if( ctxtLocks == null ) {
			log.info("[" + tenantId + "][" + clusterId + "]  -> No distributed locks to configure");
			return;
		}
		log.info("[" + tenantId + "][" + clusterId + "]  -> Starting distributed locks configuration");

		for (GGEventsContextLock ctxtLock : ctxtLocks) {
			log.info("[" + tenantId + "][" + clusterId + "]   -> Configuring " + ctxtLock.getType() + " : "
					+ ctxtLock.getName());

			IGGEventsDistributedLock alreadyExist = this.locks.get(tenantId).get(clusterId).get(ctxtLock.getName());
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
					
					throw new GGEventsException("Another DistributedLock with name " + ctxtLock.getName() + " and type "
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
				throw new GGEventsException("Cannot find DistribuedLock of type " + ctxtLock.getType() + " and version "+ctxtLock.getVersion()+".");
			}
			if (lock == null) {
				throw new GGEventsException("Cannot find DistribuedLock of type " + ctxtLock.getType() + " and version "+ctxtLock.getVersion()+".");
			}

			if (!IGGEventsDistributedLock.class.isAssignableFrom(lock)) {
				throw new GGEventsException(
						"The class [" + lock.getName() + "] must implements the IGGEventsDistributedLock interface.");
			}

			IGGEventsDistributedLock distributedLockObj = null;
			Constructor<?> ctor;
			try {
				ctor = lock.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGEventsException(e1);
			}
			try {
				distributedLockObj = (IGGEventsDistributedLock) ctor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGEventsException(e);
			}
			
			distributedLockObj.setConfiguration(ctxtLock.getConfiguration()==null?"":ctxtLock.getConfiguration(), tenantId, clusterId, this.assetId, this.objectRegistries);

			this.locks.get(tenantId).get(clusterId).put(ctxtLock.getName(), distributedLockObj);

			log.info("[" + tenantId + "][" + clusterId + "]   -> Distributed Lock registered : " + ctxtLock.getName()
					+ " of type " + ctxtLock.getType()+" and version "+ctxtLock.getVersion());
		}
		ctxtLocks.removeAll(toBeDeleted);
	}

	private void initConnectors(IGGEventsContext context) throws GGEventsException {
		log.info("[" + context.getTenantId() + "][" + context.getClusterId() + "] ==== CREATING CONNECTORS ====");

		List<IGGEventsContextConnector> ctxtConnectors = context.getConnectors();
		
		List<IGGEventsContextConnector> toBeDeleted = new ArrayList<IGGEventsContextConnector>();

		log.info("[" + context.getTenantId() + "][" + context.getClusterId() + "]  -> Starting connector configuration");

		for (IGGEventsContextConnector ctxtConnector : ctxtConnectors) {
			log.info("[" + context.getTenantId() + "][" + context.getClusterId() + "]   -> Configuring " + ctxtConnector.getType() + " : "
					+ ctxtConnector.getName());

			IGGEventsConnector alreadyExist = this.connectors.get(tenantId).get(clusterId).get(ctxtConnector.getName());
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
					throw new GGEventsException("Another Connector with name " + ctxtConnector.getName() + " and type "
							+ alreadyExist.getType() + " is already registered with a different configuration : ["
							+ config + "] vs [" + aeConfig + "]");
				} else {
					log.info("[" + context.getTenantId() + "][" + context.getClusterId() + "]   -> Connector already registered : "
							+ ctxtConnector.getName() + " of type " + ctxtConnector.getType());
					toBeDeleted.add(ctxtConnector);
					continue;
				}
			}
			Class<?> connector = null;
			try {
				connector = this.connectorClasses.get(ctxtConnector.getType()).get(ctxtConnector.getVersion());
			} catch (Exception e) {
				throw new GGEventsException("Cannot find connector of type " + ctxtConnector.getType() + " and version "+ctxtConnector.getVersion()+".");
			}
			if (connector == null) {
				throw new GGEventsException("Cannot find connector of type " + ctxtConnector.getType() + " and version "+ctxtConnector.getVersion()+".");
			}

			if (!IGGEventsConnector.class.isAssignableFrom(connector)) {
				throw new GGEventsException(
						"The class [" + connector.getName() + "] must implements the IGGEventsConnector interface.");
			}

			IGGEventsConnector connectorObj = null;
			Constructor<?> ctor;
			try {
				ctor = connector.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGEventsException(e1);
			}
			try {
				connectorObj = (IGGEventsConnector) ctor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGEventsException(e);
			}
			connectorObj.setPoolExecutor(this.executorService);
			connectorObj.setConfiguration(ctxtConnector.getConfiguration()==null?"":ctxtConnector.getConfiguration(), tenantId, clusterId, this.assetId, this.objectRegistries);
			connectorObj.setName(ctxtConnector.getName());
			
			if( connectorObj instanceof GGEventsEventsConnector) {
				this.registerEventHandler((GGEventsEventsConnector)connectorObj);
			}
			
			this.connectors.get(tenantId).get(clusterId).put(ctxtConnector.getName(), connectorObj);

			log.info("[" + context.getTenantId() + "][" + context.getClusterId() + "]   -> Connector registered : " + ctxtConnector.getName()
					+ " of type " + ctxtConnector.getType()+" and version "+ctxtConnector.getVersion());
		}
		
		ctxtConnectors.removeAll(toBeDeleted);
		
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
						this.raiseEvent(new GGEventsEvent("Unable to start Garganttua Framework", GGEventsEventCriticity.FATAL, GGEventsExecutionStage.STARTUP, new GGEventsException(e)));
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
						this.raiseEvent(new GGEventsEvent("Unable to start Garganttua Framework", GGEventsEventCriticity.FATAL, GGEventsExecutionStage.STARTUP, new GGEventsException(e)));
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
						this.raiseEvent(new GGEventsEvent("Unable to start Garganttua Framework", GGEventsEventCriticity.FATAL, GGEventsExecutionStage.STARTUP, new GGEventsException(e)));
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
						this.raiseEvent(new GGEventsEvent("Unable to start Garganttua Framework", GGEventsEventCriticity.FATAL, GGEventsExecutionStage.STARTUP, new GGEventsException(e)));
					}
					log.debug("[" + tenantId + "][" + clusterId + "] Started route " + routeId);
				});
			});
		});
		this.raiseEvent(new GGEventsEvent("Garganttua Core Context Engine started", GGEventsEventCriticity.INFO, GGEventsExecutionStage.STARTUP, null));
		
	}

	/**
	 * 
	 * @param GGEventsEvent
	 */
	private void raiseEvent(GGEventsEvent GGEventsEvent) {
		this.executorService.execute(new Thread() {
			@Override
			public void run() {
				eventsHandlers.forEach(eh -> {
					executorService.execute(new Thread() {
						@Override
						public void run() {
							eh.handleEvent(GGEventsEvent);
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
						this.raiseEvent(new GGEventsEvent("Unable to stop Garganttua Framework", GGEventsEventCriticity.FATAL, GGEventsExecutionStage.SHUTDOWN, new GGEventsException(e)));
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
						this.raiseEvent(new GGEventsEvent("Unable to stop Garganttua Framework", GGEventsEventCriticity.FATAL, GGEventsExecutionStage.SHUTDOWN, new GGEventsException(e)));
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
	public List<IGGEventsConnector> getConnectors() {
		ArrayList<IGGEventsConnector> c = new ArrayList<IGGEventsConnector>();
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
	public void registerContextSourceConfiguratorRegistry(IGGEventsContextSourceConfigurationRegistry contextSourceConfigurationRegistry) {
		this.contextSourceConfigurationRegistry = contextSourceConfigurationRegistry;
	}

	public IGGEventsConnector getConnector(String tenantId, String clusterId, String name) {
		return this.connectors.get(tenantId).get(clusterId).get(name);
	}

	@Override
	public IGGEventsSubscription getSubscription(String subscriptionId, String tenantId, String clusterId) {
		return this.subscriptions.get(tenantId).get(clusterId).get(subscriptionId);
	}

	@Override
	public GGEventsAssetContext getAssetContext() {
		return new GGEventsAssetContext(this.assetId, this.now, this.assetName, this.assetVersion, this.processorDescriptors, this.connectorDescriptors, this.lockDescriptors, this.contextBuilder.getContext());
	}

	public static IGGEventsEngine engine(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
