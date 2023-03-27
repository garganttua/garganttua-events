/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context;

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
public class GGContextSubscription extends GGSourcedContextItem {
	
	public GGContextSubscription(String dataflow, String topic, String connector, GGContextPublicationMode publicationMode, boolean buffered, boolean bufferPersisted, GGContextTimeInterval timeInterval, GGContextConsumerConfiguration cconfiguration, GGContextProducerConfiguration pconfiguration, List<GGContextItemSource> sources) {
		super(sources);
		this.dataFlow = dataflow;
		this.topic = topic;
		this.connector = connector;
		this.publicationMode = publicationMode;
		this.buffered = buffered;
		this.bufferPersisted = bufferPersisted;
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
	private GGContextPublicationMode publicationMode; 
	
	@JsonProperty(value ="buffered",required = true)
	private boolean buffered = false;
	
	@JsonProperty(value ="bufferPersisted",required = true)
	private boolean bufferPersisted = false;
	
	@JsonProperty(value ="timeInterval",required = false)
	private GGContextTimeInterval timeInterval;
	
	@JsonProperty(value ="consumerConfiguration",required = false)
	private GGContextConsumerConfiguration cconfiguration;
	
	@JsonProperty(value ="producerConfiguration",required = false)
	private GGContextProducerConfiguration pconfiguration;
	
	@JsonIgnore
//	@JsonProperty(value ="uuid",required = false)
	public String getId() {
		String subscriptionId = connector+"://"+this.getDataFlow()+this.getTopic();
		return subscriptionId;
	}
}
