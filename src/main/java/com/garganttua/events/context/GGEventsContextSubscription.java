/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.concurrent.TimeUnit;

import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConsumerConfiguration;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextProducerConfiguration;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTimeInterval;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 
 * 
 * 
 *
 */
public class GGEventsContextSubscription extends GGEventsContextSourcedItem<IGGEventsContextSubscription> implements IGGEventsContextSubscription {
	
	@Getter
	private String dataflow; 
	
	@Getter
	private String topic;
	
	@Getter
	private String connector;
	
	@Getter
	private GGEventsContextPublicationMode publicationMode; 
	
	@Getter
	private IGGEventsContextTimeInterval timeInterval;
	
	@Getter
	private IGGEventsContextConsumerConfiguration cconfiguration;
	
	@Getter
	private IGGEventsContextProducerConfiguration pconfiguration;

	@Setter
	private IGGEventsContext context;
	
	public GGEventsContextSubscription(String dataflow, String topic, String connector, GGEventsContextPublicationMode publicationMode) {
		this(dataflow, topic, connector, publicationMode, new GGEventsContextTimeInterval(1L, TimeUnit.MINUTES), new GGEventsContextConsumerConfiguration(), new GGEventsContextProducerConfiguration());
	}
	
	public GGEventsContextSubscription(String dataflow, String topic, String connector, GGEventsContextPublicationMode publicationMode, IGGEventsContextTimeInterval timeInterval, IGGEventsContextConsumerConfiguration cconfiguration, IGGEventsContextProducerConfiguration pconfiguration) {
		this.dataflow = dataflow;
		this.topic = topic;
		this.connector = connector;
		this.publicationMode = publicationMode;
		this.timeInterval = timeInterval;
		this.cconfiguration = cconfiguration;
		this.pconfiguration = pconfiguration;
	}

	@Override
	public IGGEventsContextSubscription producerConfiguration(GGEventsContextDestinationPolicy destinationPolicy, String destinationUuid) {
		return this.producerConfiguration(new GGEventsContextProducerConfiguration(destinationPolicy, destinationUuid));		
	}
	
	@Override
	public IGGEventsContextSubscription consumerConfiguration(GGEventsContextDataflowInProcessMode inProcessMode,
			GGEventsContextOriginPolicy originPolicy, GGEventsContextDestinationPolicy destinationPolicy, boolean ignoreAssetMessages,
			GGEventsContextHighAvailabilityMode haMode) {
				return this.consumerConfiguration(new GGEventsContextConsumerConfiguration(inProcessMode, originPolicy, destinationPolicy, ignoreAssetMessages, haMode));
	}
	
	@Override
	public IGGEventsContext context() {
		return this.context;
	}
	
	public String getId() {
		String subscriptionId = connector+"://"+this.dataflow+this.topic;
		return subscriptionId;
	}

	@Override
	public boolean equals(Object subscription) {
		return this.getId().equals(((GGEventsContextSubscription) subscription).getId());
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	@Override
	public void context(IGGEventsContext context) {
		this.context = context;
	}

	@Override
	public IGGEventsContextSubscription producerConfiguration(IGGEventsContextProducerConfiguration configuration) {
		this.pconfiguration = configuration;
		return this;
	}

	@Override
	public IGGEventsContextSubscription consumerConfiguration(IGGEventsContextConsumerConfiguration configuration) {
		cconfiguration = configuration;
		return this;
	}

	@Override
	public IGGEventsContextSubscription timeInterval(IGGEventsContextTimeInterval timeInterval) {
		this.timeInterval = timeInterval;
		return this;
	}

	@Override
	public IGGEventsContextSubscription timeInterval(long time, TimeUnit unit) {
		this.timeInterval(new GGEventsContextTimeInterval(time, unit));
		return this;
	}

	@Override
	protected boolean isEqualTo(IGGEventsContextSubscription item) {
		return this.equals(item) && 
				this.publicationMode == item.getPublicationMode() &&
				this.timeInterval == item.getTimeInterval() &&
				this.cconfiguration.equals(item.getCconfiguration()) &&
				this.pconfiguration.equals(item.getPconfiguration());
	}
}
