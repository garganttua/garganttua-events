package com.gtech.garganttua.core.processors;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gtech.garganttua.core.spec.annotations.GGProcessor;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGEnrichStrategy;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGConfigurationDecoder;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

@GGProcessor(type="enrich", version="1.0.0")
public class GGEnrichProcessor extends GGAbstractProcessor {

	private String configuration;
	private String tenantId;
	private String clusterId;
	private String assetId;
	private String strategyClassName;
	private IGGEnrichStrategy strategyObject;
	private Object dataSource;
	private IGGObjectRegistryHub objectRegistries;
	private String infos;
	private String manual;

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		this.strategyObject.enrich(exchange.getTenantId(), exchange, this.dataSource);
	}

	@Override
	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries)
			throws GGCoreException {
		this.configuration = configuration;
		this.tenantId = tenantId;
		this.clusterId = clusterId;
		this.assetId = assetId;
		this.objectRegistries = objectRegistries;

	}

	@Override
	public void applyConfiguration() throws GGCoreException {

		Map<String, List<String>> __configuration__ = GGConfigurationDecoder.getConfigurationFromString(this.configuration);
		
		for( Entry<String, List<String>> entry: __configuration__.entrySet() ) {
			switch(entry.getKey()) {
			case "enrichStrategy":
				try {
					this.strategyObject = (IGGEnrichStrategy) this.objectRegistries.getObject(entry.getValue().get(0));
				} catch (GGCoreException e) {
					throw e;
				}
				break;
			case "dataSource":
				try {
					this.dataSource = this.objectRegistries.getObject(entry.getValue().get(0));
				} catch (GGCoreException e) {
					throw e;
				}
				break;
			}
		}	
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "enrich", "1.0.0", this.infos, this.manual);
	}
}
