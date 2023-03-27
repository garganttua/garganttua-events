/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine.processors;

import com.gtech.garganttua.core.context.GGContextConsumerConfiguration;
import com.gtech.garganttua.core.context.GGContextDestinationPolicy;
import com.gtech.garganttua.core.context.GGContextOriginPolicy;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.Getter;

public class GGInFilterProcessor extends GGAbstractProcessor {

	@Getter
	private String configuration;
	private GGContextConsumerConfiguration consumerConfiguration;
	private String assetId;
	private String clusterId;
	private String infos;
	private String manual;

	public GGInFilterProcessor(GGContextConsumerConfiguration consumerConfiguration, String assetId, String clusterId) {
		this.consumerConfiguration = consumerConfiguration;
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.type = "IGGProcessor::GGInFilterProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
		
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		GGContextOriginPolicy originPolicy = this.consumerConfiguration.getOpolicy();
		GGContextDestinationPolicy destinationPolicy = this.consumerConfiguration.getDpolicy();
		
		if( destinationPolicy != null ) {
			switch(destinationPolicy) {
			case ONLY_TO_ASSET:
				if( !exchange.getToUuid().equals(this.assetId) ) {
					throw new GGCoreFilterException("assetId mismatch");
				}
				break;
			case ONLY_TO_CLUSTER:
				if( !exchange.getToUuid().equals(this.clusterId) ) {
					throw new GGCoreFilterException("clusterId mismatch");
				}
				break;
			case TO_ANY:
			default:
				break;	
			}
		} 
	}

	@Override
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "inFilterProcessor", "1.0.0", this.infos, this.manual);
	}

}
