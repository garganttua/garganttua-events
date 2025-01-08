package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.garganttua.events.context.GGEventsContextPublicationMode;
import com.garganttua.events.context.GGEventsContextSourcedItem;
import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class GGEventsJsonContextSubscription implements IGGEventsContextItemBinder<IGGEventsContextSubscription> {
	
	private String dataflow; 

	private String topic;

	private String connector;

	private GGEventsContextPublicationMode publicationMode; 

	private GGEventsJsonContextTimeInterval timeInterval;

	private GGEventsJsonContextConsumerConfiguration consumerConfiguration;

	private GGEventsJsonContextProducerConfiguration producerConfiguration;

	protected List<GGEventsJsonContextSourceItem> sources = new ArrayList<GGEventsJsonContextSourceItem>();

	protected List<GGEventsJsonContextSubscription> otherVersions = new ArrayList<GGEventsJsonContextSubscription>();
	
	@Override
	public IGGEventsContextSubscription bind() throws GGEventsException {
		GGEventsContextSubscription subscription = new GGEventsContextSubscription(this.dataflow, this.topic, this.connector, this.publicationMode);
		if( this.timeInterval != null) 
			subscription.timeInterval(this.timeInterval.bind());
		if( this.consumerConfiguration != null) 
			subscription.consumerConfiguration(this.consumerConfiguration.bind());
		if( this.producerConfiguration != null) 
			subscription.producerConfiguration(this.producerConfiguration.bind());
		GGEventsJsonContextSourceItem.bindSources(subscription, this.sources);
		GGEventsJsonContextSourceItem.bindOtherVersions(subscription, this.otherVersions);
		return subscription;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void build(IGGEventsContextSubscription bound) throws GGEventsException {
		this.dataflow = bound.getDataflow();
		this.topic = bound.getTopic();
		this.connector = bound.getConnector();
		this.publicationMode = bound.getPublicationMode();
		this.timeInterval = new GGEventsJsonContextTimeInterval();
		this.timeInterval.build(bound.getTimeInterval());
		this.consumerConfiguration = new GGEventsJsonContextConsumerConfiguration();
		this.consumerConfiguration.build(bound.getCconfiguration());
		this.producerConfiguration = new GGEventsJsonContextProducerConfiguration();
		this.producerConfiguration.build(bound.getPconfiguration());
		GGEventsJsonContextSourceItem.buildSources((GGEventsContextSourcedItem<?>) bound, this.sources);
		GGEventsJsonContextSourceItem.buildOtherVersions((GGEventsContextSourcedItem<IGGEventsContextSubscription>) bound, this.otherVersions, GGEventsJsonContextSubscription.class);
	}

}
