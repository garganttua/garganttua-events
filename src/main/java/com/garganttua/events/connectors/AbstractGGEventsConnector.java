package com.garganttua.events.connectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.garganttua.events.spec.exceptions.GGEventsConnectorException;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsConnectorConsumerRegistrationRequest;
import com.garganttua.events.spec.objects.GGEventsConnectorProducerRegistrationRequest;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGGEventsConnector implements IGGEventsConnector {

	private static final String ACONNECTOR_POLL_INTERVAL = "pollInterval";
	private static final String ACONNECTOR_POLL_INTERVAL_UNIT = "pollIntervalUnit";
	
	protected String configuration;
	protected String tenantId;
	protected String clusterId;
	protected String assetId;
	protected IGGEventsObjectRegistryHub objectRegistries;
	protected IGGEventsEngine engine;
	protected String name;
	protected ExecutorService poolExecutor;
	protected Map<String, AbstractGGEventsConsumer> consumers = new HashMap<String, AbstractGGEventsConsumer>();
	protected Map<String, AbstractGGEventsProducer> producers = new HashMap<String, AbstractGGEventsProducer>();
	protected Integer pollInterval = 10;
	protected TimeUnit pollIntervalUnit = TimeUnit.SECONDS;
	protected List<GGEventsConnectorConsumerRegistrationRequest> consumerRequests = new ArrayList<GGEventsConnectorConsumerRegistrationRequest>();
	protected List<GGEventsConnectorProducerRegistrationRequest> producerRequests = new ArrayList<GGEventsConnectorProducerRegistrationRequest>();

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}][Topic:{}] Sending message {}", this.name, exchange.getToTopic(), exchange);
		try {
			this.producers.get(exchange.getToTopic()).publishValue(exchange.getValue());
		} catch (GGEventsProcessingException e) {
			throw new GGEventsHandlingException(e);
		}
		return true;
	}

	@Override
	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine) throws GGEventsException {
		this.configuration = configuration;
		this.tenantId = tenantId;
		this.clusterId = clusterId;
		this.assetId = assetId;
		this.objectRegistries = objectRegistries;
		this.engine = engine;
		
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(configuration);
		__configuration__.forEach((name, values) -> { 
			switch(name) {
			case ACONNECTOR_POLL_INTERVAL:
				this.pollInterval = Integer.valueOf(values.get(0));
				break;
			case ACONNECTOR_POLL_INTERVAL_UNIT:
				this.pollIntervalUnit = TimeUnit.valueOf(values.get(0));
				break;
			}
		});
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setPoolExecutor(ExecutorService poolExecutor) {
		this.poolExecutor = poolExecutor;
	}

	@Override
	public void registerConsumer(GGEventsConnectorConsumerRegistrationRequest request) {
		this.consumerRequests.add(request);
	}
	
	@Override
	public void registerProducer(GGEventsConnectorProducerRegistrationRequest request) {
		this.producerRequests.add(request);
	}

	public void doRegisterConsumer(GGEventsConnectorConsumerRegistrationRequest request) {
		String topicRef = request.subscription().getTopic();
		String dataflowUuid = request.dataflow().getUuid();
		boolean garanteeOrder = request.dataflow().isGaranteeOrder();
		
		log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}] Registering consumer dataflowUuid {} topic {} garantee order {}", this.name, dataflowUuid, topicRef, garanteeOrder);
		
		AbstractGGEventsConsumer consumer = this.consumers.get(topicRef);
		
		if( consumer == null ) {
			consumer = this.createConsumer(request.dataflow(), request.subscription(), request.messageHandler());
			if( consumer != null ) {
				this.consumers.put(topicRef, consumer);	
			} else {
				log.warn("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}] Unable to create consumer dataflowUuid {} topic {} garantee order {}", this.name, dataflowUuid, topicRef, garanteeOrder);
			}
		}
		
		consumer.setGaranteeOrder(dataflowUuid, garanteeOrder);
		consumer.registerHandler(dataflowUuid, request.messageHandler());
	}


	public void doRegisterProducer(GGEventsConnectorProducerRegistrationRequest request) {
		String topicRef = request.subscription().getTopic();
		String dataflowUuid = request.dataflow().getUuid();
		log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}] Registering producer dataflowUuid {} topic {}", this.name, dataflowUuid, topicRef);
		
		AbstractGGEventsProducer producer = this.producers.get(dataflowUuid);
		
		if( producer == null ) {
			producer = this.createProducer(request.dataflow(), request.subscription());
			if(producer != null ) {
				this.producers.put(topicRef, producer);	
			} else {
				log.warn("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}] Unable to create producer dataflowUuid {} topic {}", this.name, dataflowUuid, topicRef);
			}
		}
	}
	
	protected abstract AbstractGGEventsProducer createProducer(IGGEventsContextDataflow dataflow,
			IGGEventsContextSubscription subscription);

	@Override
	public void stop() throws GGEventsConnectorException {
		log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}] Stoping", this.name);
		this.doStop();
		this.consumers.forEach((s, c) -> {
			c.stop();
		});
		log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}] Stopped", this.name);
	}
	
	@Override
	public void start() throws GGEventsConnectorException {
		log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}] Starting", this.name);
		
		this.consumerRequests.forEach(request -> {
			doRegisterConsumer(request);
		});
		
		this.producerRequests.forEach(request -> {
			doRegisterProducer(request);
		});
		
		this.consumers.forEach((s, c) -> {
			this.poolExecutor.execute(c);
		});
		this.doStart();
		log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}] Started", this.name);
	}

	protected abstract AbstractGGEventsConsumer createConsumer(IGGEventsContextDataflow dataflow, IGGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler);
	protected abstract void doStop();
	protected abstract void doStart();

}
