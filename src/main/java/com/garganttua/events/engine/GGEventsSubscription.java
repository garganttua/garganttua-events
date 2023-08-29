/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import com.garganttua.events.context.GGEventsContextPublicationMode;
import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.engine.consumers.GGEventsOnChangeConsumer;
import com.garganttua.events.engine.processors.GGEventsEncapsulatedProtocolInProcessor;
import com.garganttua.events.engine.processors.GGEventsEncapsulatedProtocolOutProcessor;
import com.garganttua.events.engine.processors.GGEventsInFilterProcessor;
import com.garganttua.events.engine.processors.GGEventsOutFilterProcessor;
import com.garganttua.events.engine.producers.GGEventsOnChangeProducer;
import com.garganttua.events.engine.producers.GGEventsTimeIntervalProducer;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsConsumer;
import com.garganttua.events.spec.interfaces.IGGEventsDataflow;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.IGGEventsProducer;
import com.garganttua.events.spec.interfaces.IGGEventsSubscription;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConsumerConfiguration;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextProducerConfiguration;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;

import lombok.Getter;

@Getter
public class GGEventsSubscription implements IGGEventsSubscription {

	@Getter
	private IGGEventsDataflow dataflow;
	private String id;
	
	private IGGEventsContextSubscription subscription;
	private IGGEventsConnector connector;
	
	@Getter
	private GGEventsTopic topic;
	private GGEventsInFilterProcessor inFilter;
	private GGEventsOutFilterProcessor outFilter;
	private GGEventsOnChangeConsumer consumer;
	private IGGEventsProducer producer;
	private String assetId;
	private String clusterId;
	private IGGEventsProcessor protocolInProcessor;
	private IGGEventsProcessor protocolOutProcessor;
	
	public GGEventsSubscription(IGGEventsDataflow dataflow, IGGEventsContextSubscription subscription, IGGEventsConnector connector, GGEventsTopic topic, String assetId, String clusterId) {
		this.subscription = subscription;
		this.connector = connector;
		this.topic = topic;
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.id = subscription.getId();	
		this.dataflow = dataflow;
		
		IGGEventsContextConsumerConfiguration consumerConfiguration = this.subscription.getCconfiguration();
		this.inFilter = new GGEventsInFilterProcessor(consumerConfiguration, assetId, clusterId, dataflow.getVersion());
		
		IGGEventsContextProducerConfiguration producerConfiguration = this.subscription.getPconfiguration();
		this.outFilter = new GGEventsOutFilterProcessor(producerConfiguration);
		
		if( this.subscription.getPublicationMode() == GGEventsContextPublicationMode.ON_CHANGE ) {
			this.consumer = new GGEventsOnChangeConsumer(this.subscription.getId());
			this.producer = new GGEventsOnChangeProducer(this.subscription.getId(), this.connector);
		} else if(this.subscription.getPublicationMode() == GGEventsContextPublicationMode.TIME_INTERVAL ) {
//			this.consumer = new GGEventsTimeIntervalConsumer(this.subscription.getId());
			this.producer = new GGEventsTimeIntervalProducer(this.subscription.getId(), this.connector, this.subscription.getTimeInterval());
		}
		
		if( dataflow.isEncapsulated() ) {
			this.protocolInProcessor = new GGEventsEncapsulatedProtocolInProcessor(this.assetId, this.clusterId, this.getId(), this.getDataflow().getVersion());
			this.protocolOutProcessor = new GGEventsEncapsulatedProtocolOutProcessor(this.assetId, this.clusterId, this.topic.getRef(), this.dataflow.getVersion(), this.dataflow.getUuid(), this.connector.getName());
		} else {
			
		}
	}

	@Override
	public IGGEventsProcessor getProtocolInProcessor() {
		return this.protocolInProcessor;
	}
	
	@Override
	public IGGEventsProcessor getProtocolOutProcessor() {
		return this.protocolOutProcessor;
	}
	
	@Override
	public IGGEventsProcessor getInFilterProcessor() {
		return this.inFilter;
	}

	@Override
	public IGGEventsProcessor getOutFilterProcessor() {
		return this.outFilter;
	}

	@Override
	public IGGEventsConsumer getConsumer() {
		return this.consumer;
	}

	@Override
	public IGGEventsProducer getProducer() {
		return this.producer;
	}

	public static String getConnectorFromSubscriptionId(String subscriptionUrl) {
		return subscriptionUrl.split("://")[0];
	}

	public static String getDataflowIdFromSubscriptionId(String subscriptionUrl) {
		return subscriptionUrl.split("://")[1].split("/")[0];
	}

}
