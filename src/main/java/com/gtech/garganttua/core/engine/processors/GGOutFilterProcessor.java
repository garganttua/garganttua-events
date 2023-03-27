/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine.processors;

import com.gtech.garganttua.core.context.GGContextDestinationPolicy;
import com.gtech.garganttua.core.context.GGContextProducerConfiguration;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.Getter;

public class GGOutFilterProcessor extends GGAbstractProcessor {

	@Getter
	private String configuration;
	private GGContextProducerConfiguration producerConfiguration;
	private String infos;
	private String manual;
	
	public GGOutFilterProcessor(GGContextProducerConfiguration producerConfiguration) {
		this.producerConfiguration = producerConfiguration;
		this.type = "IGGProcessor::GGOutFilterProcessor";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		String toUuid = this.producerConfiguration.getDestinationUuid();

		GGContextDestinationPolicy dpolicy = this.producerConfiguration.getDpolicy();
		
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
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "outFilterProcessor", "1.0.0", this.infos, this.manual);
	}

}
