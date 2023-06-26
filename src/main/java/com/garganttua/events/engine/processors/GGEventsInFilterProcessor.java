/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import com.garganttua.events.context.GGEventsContextConsumerConfiguration;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextOriginPolicy;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsAbstractProcessor;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;

public class GGEventsInFilterProcessor extends GGEventsAbstractProcessor {

	@Getter
	private String configuration;
	private GGEventsContextConsumerConfiguration consumerConfiguration;
	private String assetId;
	private String clusterId;
	private String infos;
	private String manual;

	public GGEventsInFilterProcessor(GGEventsContextConsumerConfiguration consumerConfiguration, String assetId, String clusterId) {
		this.consumerConfiguration = consumerConfiguration;
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.type = "IGGEventsProcessor::GGEventsInFilterProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
		
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		GGEventsContextOriginPolicy originPolicy = this.consumerConfiguration.getOpolicy();
		GGEventsContextDestinationPolicy destinationPolicy = this.consumerConfiguration.getDpolicy();
		
		if( destinationPolicy != null ) {
			switch(destinationPolicy) {
			case ONLY_TO_ASSET:
				if( !exchange.getToUuid().equals(this.assetId) ) {
					throw new GGEventsCoreFilterException("assetId mismatch");
				}
				break;
			case ONLY_TO_CLUSTER:
				if( !exchange.getToUuid().equals(this.clusterId) ) {
					throw new GGEventsCoreFilterException("clusterId mismatch");
				}
				break;
			case TO_ANY:
			default:
				break;	
			}
		} 
	}

	@Override
	public void applyConfiguration() throws GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "inFilterProcessor", "1.0.0", this.infos, this.manual);
	}

}
