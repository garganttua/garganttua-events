package com.garganttua.events.processors;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsEnrichStrategy;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsAbstractProcessor;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

@GGEventsProcessor(type="enrich", version="1.0.0")
public class GGEventsEnrichProcessor extends GGEventsAbstractProcessor {

	private String configuration;
	private String tenantId;
	private String clusterId;
	private String assetId;
	private String strategyClassName;
	private IGGEventsEnrichStrategy strategyObject;
	private Object dataSource;
	private IGGEventsObjectRegistryHub objectRegistries;
	private String infos;
	private String manual;

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		this.strategyObject.enrich(exchange.getTenantId(), exchange, this.dataSource);
	}

	@Override
	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries)
			throws GGEventsCoreException {
		this.configuration = configuration;
		this.tenantId = tenantId;
		this.clusterId = clusterId;
		this.assetId = assetId;
		this.objectRegistries = objectRegistries;

	}

	@Override
	public void applyConfiguration() throws GGEventsCoreException {

		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(this.configuration);
		
		for( Entry<String, List<String>> entry: __configuration__.entrySet() ) {
			switch(entry.getKey()) {
			case "enrichStrategy":
				try {
					this.strategyObject = (IGGEventsEnrichStrategy) this.objectRegistries.getObject(entry.getValue().get(0));
				} catch (GGEventsCoreException e) {
					throw e;
				}
				break;
			case "dataSource":
				try {
					this.dataSource = this.objectRegistries.getObject(entry.getValue().get(0));
				} catch (GGEventsCoreException e) {
					throw e;
				}
				break;
			}
		}	
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "enrich", "1.0.0", this.infos, this.manual);
	}
}
