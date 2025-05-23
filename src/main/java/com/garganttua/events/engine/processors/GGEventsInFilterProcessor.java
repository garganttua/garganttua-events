/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextOriginPolicy;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConsumerConfiguration;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;

public class GGEventsInFilterProcessor implements IGGEventsProcessor {

	@Getter
	private String configuration;
	private IGGEventsContextConsumerConfiguration consumerConfiguration;
	private String assetId;
	private String clusterId;
	private String infos;
	private String manual;
	private Object dataflowVersion;
	private String type;

	public GGEventsInFilterProcessor(IGGEventsContextConsumerConfiguration consumerConfiguration, String assetId, String clusterId, String dataflowVersion) {
		this.consumerConfiguration = consumerConfiguration;
		this.assetId = assetId;
		this.clusterId = clusterId;
		this.dataflowVersion = dataflowVersion;
		this.type = "processor::in-filter";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine) {
		this.configuration = configuration;
		
	}

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		GGEventsContextOriginPolicy originPolicy = this.consumerConfiguration.getOpolicy();
		GGEventsContextDestinationPolicy destinationPolicy = this.consumerConfiguration.getDpolicy();
		
		//Check dataflow Version 
		if( exchange.getDataflowVersion() == null || !exchange.getDataflowVersion().equals(this.dataflowVersion) ) {
			throw new GGEventsHandlingException("version mismatch");
		}
		
		if( destinationPolicy != null ) {
			switch(destinationPolicy) {
			case ONLY_TO_ASSET:
				if( !exchange.getToUuid().equals(this.assetId) ) {
					throw new GGEventsHandlingException("assetId mismatch");
				}
				break;
			case ONLY_TO_CLUSTER:
				if( !exchange.getToUuid().equals(this.clusterId) ) {
					throw new GGEventsHandlingException("clusterId mismatch");
				}
				break;
			case TO_ANY:
			default:
				break;	
			}
		}
		return true; 
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "inFilterProcessor", "1.0", this.infos, this.manual);
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExecutorService(ExecutorService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScheduledExecutorService(ScheduledExecutorService service) {
		// TODO Auto-generated method stub
		
	}

}
