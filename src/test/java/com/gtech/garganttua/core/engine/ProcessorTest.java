/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import com.gtech.garganttua.core.spec.annotations.GGProcessor;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GGProcessor(type="GGProcessorTest", version="1.0.0")
public class ProcessorTest extends GGAbstractProcessor {

	private String type;
	
	@Override
	public void handle(GGExchange exchange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

}
