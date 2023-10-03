package com.garganttua.events.processors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;

@GGEventsProcessor(type="setHeader", version="1.0")
public class GGEventsSetHeaderProcessor implements IGGEventsProcessor {

	@Getter
	private String configuration;
	private String headerName;
	private String headerValue;
	private String infos;
	private String manual;
	private String type = "processor::set-header";

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine) {
		this.configuration = configuration;
		
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(configuration);
		
		__configuration__.forEach((k,v) -> {
			headerName = k;
			headerValue = v.get(0);
		});
	}

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		if( exchange.isVariable(this.headerValue)) {
			try {
				exchange.getHeaders().put(this.headerName, GGEventsExchange.getVariableValue(exchange, this.headerValue));
			} catch (GGEventsException e) {
				throw new GGEventsHandlingException(e);
			}
		} else {
			exchange.getHeaders().put(this.headerName, this.headerValue);
		}
		return true;
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "setHeader", "1.0", this.infos, this.manual);
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
