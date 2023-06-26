/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine.processors;

import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextProducerConfiguration;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsAbstractProcessor;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;

public class GGEventsOutFilterProcessor extends GGEventsAbstractProcessor {

	@Getter
	private String configuration;
	private GGEventsContextProducerConfiguration producerConfiguration;
	private String infos;
	private String manual;
	
	public GGEventsOutFilterProcessor(GGEventsContextProducerConfiguration producerConfiguration) {
		this.producerConfiguration = producerConfiguration;
		this.type = "IGGEventsProcessor::GGEventsOutFilterProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		String toUuid = this.producerConfiguration.getDestinationUuid();

		GGEventsContextDestinationPolicy dpolicy = this.producerConfiguration.getDpolicy();
		
		if( dpolicy != null ) {
			switch( dpolicy ) {
			case ONLY_TO_ASSET:
			case ONLY_TO_CLUSTER:
				if( exchange.isVariable(toUuid) ) {
					toUuid = exchange.getVariableValue(exchange, toUuid);
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
	public void applyConfiguration() throws GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "outFilterProcessor", "1.0.0", this.infos, this.manual);
	}

}
