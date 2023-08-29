package com.garganttua.events.engine;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.spec.enums.GGEventsEventCriticity;
import com.garganttua.events.spec.enums.GGEventsExecutionStage;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsAssetInfos;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsDistributedLock;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConfigurable;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextNamable;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTypable;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextVersionable;
import com.garganttua.events.spec.objects.GGEventsEvent;
import com.garganttua.events.spec.objects.GGEventsUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsEngine implements IGGEventsEngine {

	private String assetId;
	private Map<String, Map<String, IGGEventsContext>> contexts;
	private List<IGGEventsEventHandler> eventsHandlers;
	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;
	private Map<String, Map<String, Class<?>>> connectorObjs;
	private Map<String, Map<String, Class<?>>> lockObjs;
	private Map<String, Map<String, Class<?>>> processorObjs;
	
	private Map<String, Map<String, Map<String, IGGEventsConnector>>> connectors;
	private Map<String, Map<String, Map<String, IGGEventsProcessor>>> processors;
	private Map<String, Map<String, Map<String, GGEventsTopic>>> topics;
	private Map<String, Map<String, Map<String, GGEventsDataflow>>> dataflows;
	private Map<String, Map<String, Map<String, GGEventsSubscription>>> subscriptions;
	private Map<String, Map<String, Map<String, GGEventsRoute>>> routes;
	private Map<String, Map<String, Map<String, IGGEventsDistributedLock>>> locks;

	public GGEventsEngine(String assetId, Map<String, Map<String, IGGEventsContext>> contexts,
			List<IGGEventsEventHandler> eventsHandlers, ExecutorService executorService,
			ScheduledExecutorService scheduledExecutorService, Map<String, Map<String, Class<?>>> connectors,
			Map<String, Map<String, Class<?>>> locks, Map<String, Map<String, Class<?>>> processors) {
		log.info("============================================");
		log.info("======                                ======");
		log.info("====== Starting Garganttua Events     ======");
		log.info("======                                ======");
		log.info("============================================");
		log.info("==== ASSET [" + assetId + "] ====");
		log.info("==== CONTEXT CONSTRUCTION ====");
		this.assetId = assetId;
		this.contexts = contexts;
		this.eventsHandlers = eventsHandlers;
		this.executorService = executorService;
		this.scheduledExecutorService = scheduledExecutorService;
		this.connectorObjs = connectors;
		this.lockObjs = locks;
		this.processorObjs = processors;
		
		this.connectors = new HashMap<String, Map<String, Map<String, IGGEventsConnector>>>();
		this.processors = new HashMap<String, Map<String, Map<String, IGGEventsProcessor>>>();
		this.topics = new HashMap<String, Map<String, Map<String, GGEventsTopic>>>();
		this.dataflows = new HashMap<String, Map<String, Map<String, GGEventsDataflow>>>();
		this.subscriptions = new HashMap<String, Map<String, Map<String, GGEventsSubscription>>>();
		this.routes = new HashMap<String, Map<String, Map<String, GGEventsRoute>>>();
		this.locks = new HashMap<String, Map<String, Map<String, IGGEventsDistributedLock>>>();
		
		this.init();
	}

	private void init() {
		this.raiseEvent(new GGEventsEvent("Context construction", GGEventsEventCriticity.INFO, GGEventsExecutionStage.INIT, null));
		
		this.contexts.forEach((tenantId, cluster) -> {
			log.info("==== TENANT [" + tenantId + "] ====");
			
			this.connectors.put(tenantId, new HashMap<String, Map<String, IGGEventsConnector>>());
			this.processors.put(tenantId, new HashMap<String, Map<String, IGGEventsProcessor>>());
			this.topics.put(tenantId, new HashMap<String, Map<String, GGEventsTopic>>());
			this.dataflows.put(tenantId, new HashMap<String, Map<String, GGEventsDataflow>>());
			this.subscriptions.put(tenantId, new HashMap<String, Map<String, GGEventsSubscription>>());
			this.routes.put(tenantId, new HashMap<String, Map<String, GGEventsRoute>>());
			this.locks.put(tenantId, new HashMap<String, Map<String,IGGEventsDistributedLock>>());
			
			cluster.forEach((clusterId, context) -> {

				log.info("==== CLUSTER [" + clusterId + "] ====");
				this.connectors.get(tenantId).put(clusterId, new HashMap<String, IGGEventsConnector>());
				this.processors.get(tenantId).put(clusterId, new HashMap<String, IGGEventsProcessor>());
				this.topics.get(tenantId).put(clusterId, new HashMap<String, GGEventsTopic>());
				this.dataflows.get(tenantId).put(clusterId, new HashMap<String, GGEventsDataflow>());
				this.subscriptions.get(tenantId).put(clusterId, new HashMap<String, GGEventsSubscription>());
				this.routes.get(tenantId).put(clusterId, new HashMap<String, GGEventsRoute>());
				this.locks.get(tenantId).put(clusterId, new HashMap<String, IGGEventsDistributedLock>());
				
				log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] ==== CREATING CONNECTORS ====");
				this.initObjects(assetId, tenantId, clusterId, context.getConnectors(), this.connectorObjs, this.connectors.get(tenantId).get(clusterId));
				log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] ==== CREATING DISTRIBUTED LOCKS ====");
				this.initObjects(assetId, tenantId, clusterId, context.getLocks(), this.lockObjs, this.locks.get(tenantId).get(clusterId));
				log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] ==== CREATING TOPICS ====");
				this.initTopics(context, tenantId, clusterId);
				log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] ==== CREATING DATAFLOWS ====");
				this.initDataflows(context, tenantId, clusterId);
				log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] ==== CREATING SUSBCRIPTIONS ====");
				this.initSubscriptions(context, tenantId, clusterId);
				
			});
		});
	}
	
	private void initTopics(IGGEventsContext context, String tenantId, String clusterId) {
		context.getTopics().forEach(topic -> {
			log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] -> Topic " + topic.getRef() + " registered"); 
			this.topics.get(tenantId).get(clusterId).put(topic.getRef(), new GGEventsTopic(topic));
		});
	}
	
	private void initDataflows(IGGEventsContext context, String tenantId, String clusterId) {
		context.getDataflows().forEach(dataflow -> {
			log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] -> Dataflow " + dataflow.getUuid() + " "
					+ dataflow.getName() + " " + dataflow.getType() + " " + dataflow.getVersion()+ " registered");
			
			try {
				GGEventsUtils.checkVersion(dataflow.getVersion());
			} catch (GGEventsException e) {
				throw new IllegalArgumentException(e);
			}
			this.dataflows.get(tenantId).get(clusterId).put(dataflow.getUuid(), new GGEventsDataflow(dataflow));
		});
	}
	
	private void initSubscriptions(IGGEventsContext context, String tenantId, String clusterId) {

		for (IGGEventsContextSubscription subscription : context.getSubscriptions()) {
			String connectorName = subscription.getConnector();
			IGGEventsConnector connector = null;
			try {
				connector = this.connectors.get(tenantId).get(clusterId).get(connectorName);
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot construct subscription " + subscription.getId()
				+ " : the connector " + connectorName + " is not registered");
			}

			if (connector == null) {
				throw new IllegalArgumentException("Cannot construct subscription " + subscription.getId()
						+ " : the connector " + connectorName + " is not registered");
			}

			String dataflowId = subscription.getDataflow();
			GGEventsDataflow dataflow = this.dataflows.get(tenantId).get(clusterId).get(dataflowId);

			if (dataflow == null) {
				throw new IllegalArgumentException("Cannot construct subscription " + subscription.getId()
						+ " : the dataflow " + dataflowId + " is not registered");
			}

			String topicRef = subscription.getTopic();
			GGEventsTopic topic = this.topics.get(tenantId).get(clusterId).get(topicRef);

			if (topic == null) {
				throw new IllegalArgumentException("Cannot construct subscription " + subscription.getId() + " : the topic "
						+ topicRef + " is not registered");
			}

			GGEventsSubscription s = new GGEventsSubscription(dataflow, subscription, connector, topic, this.assetId, clusterId);

			if( this.subscriptions.get(tenantId).get(clusterId).get(subscription.getId()) != null ) {
				log.error("Cannot register subscription " + subscription.getId() + " : already registered");
				throw new IllegalArgumentException("Cannot register subscription " + subscription.getId() + " : already registered");
			}
			
			this.subscriptions.get(tenantId).get(clusterId).put(subscription.getId(), s);

			log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] -> Subscription " + subscription.getId()
					+ " registered, publicationMode=" + subscription.getPublicationMode());
		}
	}

	@SuppressWarnings({ "unchecked", "null" })
	private <T,U,V> void initObjects(String assetId, String tenantId, String clusterId, List<T> ctxtObjects, Map<String, Map<String, U>> classes, Map<String, V> objects) {
		
		for (T ctxtObject : ctxtObjects) {
			
			String type = ((IGGEventsContextTypable) ctxtObject).getType();
			String version = ((IGGEventsContextVersionable) ctxtObject).getVersion();
			String name = ((IGGEventsContextNamable) ctxtObject).getName();
			String configuration = ((IGGEventsContextConfigurable) ctxtObject).getConfiguration();
			
			Class<U> objectClass = null;
			U object = null;
			
			try {
				objectClass = (Class<U>) classes.get(type).get(version);
			} catch (Exception e) {
				log.error("[" + assetId + "][" + tenantId + "][" + clusterId + "] Cannot find class of type " + objectClass.getName() + " with type "+type+" and version "+version+".");
				throw new IllegalArgumentException("Cannot find class of type " + objectClass.getName() + " with type "+type+" and version "+version+".");
			}
			if (objectClass == null) {
				log.error("[" + assetId + "][" + tenantId + "][" + clusterId + "] Cannot find class of type " + objectClass.getName() + " with type "+type+" and version "+version+".");
				throw new IllegalArgumentException("Cannot find class of type " + objectClass.getName() + " with type "+type+" and version "+version+".");
			}
			
			try {
				object = GGEventsUtils.getInstanceOf(objectClass);
			} catch (GGEventsException e) {
				throw new IllegalArgumentException(e);
			}

			try {
				Method setNameMethod = object.getClass().getMethod("setName", String.class);
				setNameMethod.invoke(object, name);
			} catch (Exception e) {
			}
			try {
				Method setConfigurationMethod = object.getClass().getMethod("setConfiguration", String.class, String.class, String.class, String.class, IGGEventsObjectRegistryHub.class);
				setConfigurationMethod.invoke(object, configuration, tenantId, clusterId, assetId, null);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
			try {
				Method setPoolExecutorMethod = object.getClass().getMethod("setPoolExecutor", ExecutorService.class);
				setPoolExecutorMethod.invoke(object, this.executorService);
			} catch (Exception e) {
			}

			objects.put(name, (V) object);

			log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] -> object registered : " + objectClass.getName() + " of type " + type+" and version "+version);
			
		}
	}

	@Override
	public IGGEventsEngine start() {
		return this;
	}

	@Override
	public IGGEventsEngine stop() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public IGGEventsEngine reload() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public IGGEventsAssetInfos getAssetInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	private void raiseEvent(GGEventsEvent event) {
		this.executorService.execute(new Thread() {
			@Override
			public void run() {
				eventsHandlers.forEach(e -> {
					executorService.execute(new Thread() {
						@Override
						public void run() {
							e.handleEvent(event);
						}
					});
				});
			};
		});
	}

}
