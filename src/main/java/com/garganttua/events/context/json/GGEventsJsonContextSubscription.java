package com.garganttua.events.context.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.garganttua.events.context.GGEventsContextPublicationMode;
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
	
	@JsonInclude
	private String dataflow; 
	
	@JsonInclude
	private String topic;
	
	@JsonInclude
	private String connector;
	
	@JsonInclude
	private GGEventsContextPublicationMode publicationMode; 
	
	@JsonInclude
	private GGEventsJsonContextTimeInterval timeInterval;
	
	@JsonInclude
	private GGEventsJsonContextConsumerConfiguration consumerConfiguration;
	
	@JsonInclude
	private GGEventsJsonContextProducerConfiguration producerConfiguration;

	@Override
	public IGGEventsContextSubscription bind() throws GGEventsException {
		IGGEventsContextSubscription subscription = new GGEventsContextSubscription(this.dataflow, this.topic, this.connector, this.publicationMode);
		if( this.timeInterval != null) 
			subscription.timeInterval(this.timeInterval.bind());
		if( this.consumerConfiguration != null) 
			subscription.consumerConfiguration(this.consumerConfiguration.bind());
		if( this.producerConfiguration != null) 
			subscription.producerConfiguration(this.producerConfiguration.bind());
		return subscription;
	}

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
	}

}
