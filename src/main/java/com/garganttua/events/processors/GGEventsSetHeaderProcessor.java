package com.garganttua.events.processors;

import java.util.List;
import java.util.Map;

import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsAbstractProcessor;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;

@GGEventsProcessor(type="setHeader", version="1.0.0")
public class GGEventsSetHeaderProcessor extends GGEventsAbstractProcessor {

	@Getter
	private String configuration;
	private String headerName;
	private String headerValue;
	private String infos;
	private String manual;

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
		
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(configuration);
		
		__configuration__.forEach((k,v) -> {
			headerName = k;
			headerValue = v.get(0);
		});
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		if( exchange.isVariable(this.headerValue)) {
			exchange.getHeaders().put(this.headerName, exchange.getVariableValue(exchange, this.headerValue));
		} else {
			exchange.getHeaders().put(this.headerName, this.headerValue);
		}
	}

	@Override
	public void applyConfiguration() throws GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "setHeader", "1.0.0", this.infos, this.manual);
	}
}
