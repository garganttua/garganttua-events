/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import com.gtech.garganttua.core.context.GGContextConsumerConfiguration;
import com.gtech.garganttua.core.context.GGContextProducerConfiguration;
import com.gtech.garganttua.core.context.GGContextPublicationMode;
import com.gtech.garganttua.core.context.GGContextSubscription;
import com.gtech.garganttua.core.engine.consumers.GGOnChangeConsumer;
import com.gtech.garganttua.core.engine.processors.GGInFilterProcessor;
import com.gtech.garganttua.core.engine.processors.GGOutFilterProcessor;
import com.gtech.garganttua.core.engine.producers.GGOnChangeProducer;
import com.gtech.garganttua.core.engine.producers.GGTimeIntervalProducer;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGConsumer;
import com.gtech.garganttua.core.spec.interfaces.IGGDataflow;
import com.gtech.garganttua.core.spec.interfaces.IGGProcessor;
import com.gtech.garganttua.core.spec.interfaces.IGGProducer;
import com.gtech.garganttua.core.spec.interfaces.IGGSubscription;

import lombok.Getter;

@Getter
public class GGSubscription implements IGGSubscription {

	@Getter
	private IGGDataflow dataflow;
	private String id;
	
	private GGContextSubscription subscription;
	private IGGConnector connector;
	@Getter
	private GGTopic topic;
	private GGInFilterProcessor inFilter;
	private GGOutFilterProcessor outFilter;
	private GGOnChangeConsumer consumer;
	private IGGProducer producer;
	private String assetId;
	private String clusterId;
	
	public GGSubscription(IGGDataflow dataflow, GGContextSubscription subscription, IGGConnector connector, GGTopic topic, String assetId, String clusterId) {
		this.subscription = subscription;
		this.connector = connector;
		this.topic = topic;
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.id = subscription.getId();	
		this.dataflow = dataflow;
		
		GGContextConsumerConfiguration consumerConfiguration = this.subscription.getCconfiguration();
		this.inFilter = new GGInFilterProcessor(consumerConfiguration, assetId, clusterId);
		
		GGContextProducerConfiguration producerConfiguration = this.subscription.getPconfiguration();
		this.outFilter = new GGOutFilterProcessor(producerConfiguration);
		
		if( this.subscription.getPublicationMode() == GGContextPublicationMode.ON_CHANGE ) {
			this.consumer = new GGOnChangeConsumer(this.subscription.getId());
		} else if(this.subscription.getPublicationMode() == GGContextPublicationMode.TIME_INTERVAL ) {
//			this.consumer = new GGTimeIntervalConsumer(this.subscription.getId());
		}
		
		if( this.subscription.getPublicationMode() == GGContextPublicationMode.ON_CHANGE ) {
			this.producer = new GGOnChangeProducer(this.subscription.getId(), this.connector);
		} else if(this.subscription.getPublicationMode() == GGContextPublicationMode.TIME_INTERVAL ) {
			this.producer = new GGTimeIntervalProducer(this.subscription.getId(), this.connector, this.subscription.getTimeInterval());
		}
	}

	@Override
	public IGGProcessor getConsumerProcessor() {
		return this.inFilter;
	}

	@Override
	public IGGProcessor getProducerProcessor() {
		return this.outFilter;
	}

	@Override
	public IGGConsumer getConsumer() {
		return this.consumer;
	}

	@Override
	public IGGProducer getProducer() {
		return this.producer;
	}

	public static String getConnector(String subscriptionId) {
		return subscriptionId.split("://")[0];
	}

	public static String getSubscription(String subscriptionId) {
		return subscriptionId.split("://")[1].split("/")[0];
	}

}
