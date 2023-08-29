/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConsumerConfiguration;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextProducerConfiguration;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 
 * 
 * 
 *
 */
//@Getter
//@Setter
//@JsonInclude(Include.NON_NULL)
//@NoArgsConstructor
public class GGEventsContextSubscription extends GGEventsContextItem<GGEventsContextSubscription> implements IGGEventsContextSubscription {
	
	@Getter
	private String dataflow; 
	
	@Getter
	private String topic;
	
	@Getter
	private String connector;
	
	@Getter
	private GGEventsContextPublicationMode publicationMode; 
	
	@Getter
	private GGEventsContextTimeInterval timeInterval;
	
	@Getter
	private IGGEventsContextConsumerConfiguration cconfiguration;
	
	@Getter
	private IGGEventsContextProducerConfiguration pconfiguration;

	@Setter
	private IGGEventsContext context;
	
	public GGEventsContextSubscription(String dataflow, String topic, String connector, GGEventsContextPublicationMode publicationMode, GGEventsContextTimeInterval timeInterval) {
		this(dataflow, topic, connector, publicationMode, timeInterval, new GGEventsContextConsumerConfiguration(), new GGEventsContextProducerConfiguration(), new ArrayList<GGEventsContextItemSource>());
	}
	
	public GGEventsContextSubscription(String dataflow, String topic, String connector, GGEventsContextPublicationMode publicationMode, GGEventsContextTimeInterval timeInterval, IGGEventsContextConsumerConfiguration cconfiguration, IGGEventsContextProducerConfiguration pconfiguration) {
		this(dataflow, topic, connector, publicationMode, timeInterval, cconfiguration, pconfiguration, new ArrayList<GGEventsContextItemSource>());
	}
	
	public GGEventsContextSubscription(String dataflow, String topic, String connector, GGEventsContextPublicationMode publicationMode, GGEventsContextTimeInterval timeInterval, IGGEventsContextConsumerConfiguration cconfiguration, IGGEventsContextProducerConfiguration pconfiguration, List<GGEventsContextItemSource> sources) {
		this.dataflow = dataflow;
		this.topic = topic;
		this.connector = connector;
		this.publicationMode = publicationMode;
		this.timeInterval = timeInterval;
		this.cconfiguration = cconfiguration;
		this.pconfiguration = pconfiguration;
		this.sources.addAll(sources);
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
	protected boolean isEqualTo(GGEventsContextSubscription item) {
		return this.equals(item) && 
				this.publicationMode == item.publicationMode &&
				this.timeInterval == item.timeInterval &&
				this.cconfiguration.equals(item.cconfiguration) &&
				this.pconfiguration.equals(item.pconfiguration);
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
	public String getDataflow() {
		return this.dataflow;
	}
}
