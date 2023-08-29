/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import java.util.concurrent.ExecutorService;

import com.garganttua.events.context.GGEventsContextConsumerConfiguration;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextOriginPolicy;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
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
		this.type = "IGGEventsProcessor::GGEventsInFilterProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
		
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsProcessingException, GGEventsException {
		GGEventsContextOriginPolicy originPolicy = this.consumerConfiguration.getOpolicy();
		GGEventsContextDestinationPolicy destinationPolicy = this.consumerConfiguration.getDpolicy();
		
		//Check dataflow Version 
		if( exchange.getDataflowVersion() == null || !exchange.getDataflowVersion().equals(this.dataflowVersion) ) {
			throw new GGEventsFilterException("version mismatch");
		}
		
		if( destinationPolicy != null ) {
			switch(destinationPolicy) {
			case ONLY_TO_ASSET:
				if( !exchange.getToUuid().equals(this.assetId) ) {
					throw new GGEventsFilterException("assetId mismatch");
				}
				break;
			case ONLY_TO_CLUSTER:
				if( !exchange.getToUuid().equals(this.clusterId) ) {
					throw new GGEventsFilterException("clusterId mismatch");
				}
				break;
			case TO_ANY:
			default:
				break;	
			}
		} 
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

}
