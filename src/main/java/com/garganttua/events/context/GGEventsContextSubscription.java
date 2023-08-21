/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author 
 * 
 * 
 *
 */
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class GGEventsContextSubscription extends GGEventsSourcedContextItem {
	
	public GGEventsContextSubscription(String dataflow, String topic, String connector, GGEventsContextPublicationMode publicationMode, GGEventsContextTimeInterval timeInterval, GGEventsContextConsumerConfiguration cconfiguration, GGEventsContextProducerConfiguration pconfiguration, List<GGEventsContextItemSource> sources) {
		super(sources);
		this.dataFlow = dataflow;
		this.topic = topic;
		this.connector = connector;
		this.publicationMode = publicationMode;
		this.timeInterval = timeInterval;
		this.cconfiguration = cconfiguration;
		this.pconfiguration = pconfiguration;
	}

	@JsonProperty(value ="dataflow",required = true)
	private String dataFlow; 
	
	@JsonProperty(value ="topic",required = true)
	private String topic;
	
	@JsonProperty(value ="connector",required = true)
	private String connector;
	
	@JsonProperty(value ="publicationMode",required = true)
	private GGEventsContextPublicationMode publicationMode; 
	
	@JsonProperty(value ="timeInterval",required = false)
	private GGEventsContextTimeInterval timeInterval;
	
	@JsonProperty(value ="consumerConfiguration",required = false)
	private GGEventsContextConsumerConfiguration cconfiguration;
	
	@JsonProperty(value ="producerConfiguration",required = false)
	private GGEventsContextProducerConfiguration pconfiguration;
	
	@JsonIgnore
//	@JsonProperty(value ="uuid",required = false)
	public String getId() {
		String subscriptionId = connector+"://"+this.getDataFlow()+this.getTopic();
		return subscriptionId;
	}
}
