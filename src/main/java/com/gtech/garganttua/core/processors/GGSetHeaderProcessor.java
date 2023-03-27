package com.gtech.garganttua.core.processors;

import java.util.List;
import java.util.Map;

import com.gtech.garganttua.core.spec.annotations.GGProcessor;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGConfigurationDecoder;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.Getter;

@GGProcessor(type="setHeader", version="1.0.0")
public class GGSetHeaderProcessor extends GGAbstractProcessor {

	@Getter
	private String configuration;
	private String headerName;
	private String headerValue;
	private String infos;
	private String manual;

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
		this.configuration = configuration;
		
		Map<String, List<String>> __configuration__ = GGConfigurationDecoder.getConfigurationFromString(configuration);
		
		__configuration__.forEach((k,v) -> {
			headerName = k;
			headerValue = v.get(0);
		});
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		if( exchange.isVariable(this.headerValue)) {
			exchange.getHeaders().put(this.headerName, exchange.getVariableValue(exchange, this.headerValue));
		} else {
			exchange.getHeaders().put(this.headerName, this.headerValue);
		}
	}

	@Override
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "setHeader", "1.0.0", this.infos, this.manual);
	}
}
