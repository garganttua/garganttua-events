/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import com.garganttua.events.context.GGEventsContextConsumerConfiguration;
import com.garganttua.events.context.GGEventsContextProducerConfiguration;
import com.garganttua.events.context.GGEventsContextPublicationMode;
import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.engine.consumers.GGEventsOnChangeConsumer;
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

import lombok.Getter;

@Getter
public class GGEventsSubscription implements IGGEventsSubscription {

	@Getter
	private IGGEventsDataflow dataflow;
	private String id;
	
	private GGEventsContextSubscription subscription;
	private IGGEventsConnector connector;
	@Getter
	private GGEventsTopic topic;
	private GGEventsInFilterProcessor inFilter;
	private GGEventsOutFilterProcessor outFilter;
	private GGEventsOnChangeConsumer consumer;
	private IGGEventsProducer producer;
	private String assetId;
	private String clusterId;
	
	public GGEventsSubscription(IGGEventsDataflow dataflow, GGEventsContextSubscription subscription, IGGEventsConnector connector, GGEventsTopic topic, String assetId, String clusterId) {
		this.subscription = subscription;
		this.connector = connector;
		this.topic = topic;
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.id = subscription.getId();	
		this.dataflow = dataflow;
		
		GGEventsContextConsumerConfiguration consumerConfiguration = this.subscription.getCconfiguration();
		this.inFilter = new GGEventsInFilterProcessor(consumerConfiguration, assetId, clusterId);
		
		GGEventsContextProducerConfiguration producerConfiguration = this.subscription.getPconfiguration();
		this.outFilter = new GGEventsOutFilterProcessor(producerConfiguration);
		
		if( this.subscription.getPublicationMode() == GGEventsContextPublicationMode.ON_CHANGE ) {
			this.consumer = new GGEventsOnChangeConsumer(this.subscription.getId());
		} else if(this.subscription.getPublicationMode() == GGEventsContextPublicationMode.TIME_INTERVAL ) {
//			this.consumer = new GGEventsTimeIntervalConsumer(this.subscription.getId());
		}
		
		if( this.subscription.getPublicationMode() == GGEventsContextPublicationMode.ON_CHANGE ) {
			this.producer = new GGEventsOnChangeProducer(this.subscription.getId(), this.connector);
		} else if(this.subscription.getPublicationMode() == GGEventsContextPublicationMode.TIME_INTERVAL ) {
			this.producer = new GGEventsTimeIntervalProducer(this.subscription.getId(), this.connector, this.subscription.getTimeInterval());
		}
	}

	@Override
	public IGGEventsProcessor getConsumerProcessor() {
		return this.inFilter;
	}

	@Override
	public IGGEventsProcessor getProducerProcessor() {
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

	public static String getConnector(String subscriptionId) {
		return subscriptionId.split("://")[0];
	}

	public static String getSubscription(String subscriptionId) {
		return subscriptionId.split("://")[1].split("/")[0];
	}

}
