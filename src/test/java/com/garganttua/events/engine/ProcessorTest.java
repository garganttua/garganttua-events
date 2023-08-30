/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GGEventsProcessor(type="GGEventsProcessorTest", version="1.0")
public class ProcessorTest implements IGGEventsProcessor {

	private String type;
	
	@Override
	public void handle(GGEventsExchange exchange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
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
