/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextProducerConfiguration;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;

public class GGEventsOutFilterProcessor implements IGGEventsProcessor {

	@Getter
	private String configuration;
	private IGGEventsContextProducerConfiguration producerConfiguration;
	private String infos;
	private String manual;
	private String type;
	
	public GGEventsOutFilterProcessor(IGGEventsContextProducerConfiguration producerConfiguration) {
		this.producerConfiguration = producerConfiguration;
		this.type = "processor::out-filter";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsProcessingException, GGEventsException {
		String toUuid = this.producerConfiguration.getDestinationUuid();

		GGEventsContextDestinationPolicy dpolicy = this.producerConfiguration.getDpolicy();
		
		if( dpolicy != null ) {
			switch( dpolicy ) {
			case ONLY_TO_ASSET:
			case ONLY_TO_CLUSTER:
				if( exchange.isVariable(toUuid) ) {
					toUuid = GGEventsExchange.getVariableValue(exchange, toUuid);
				}
				exchange.setToUuid(toUuid);
				break;
			case TO_ANY:
			default:
				exchange.setToUuid(null);
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
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "outFilterProcessor", "1.0", this.infos, this.manual);
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
