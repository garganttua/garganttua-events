package com.garganttua.events.spec.interfaces;

import com.garganttua.events.spec.exceptions.GGEventsException;

public interface IGGEventsConfigurable {

	String getConfiguration();

	void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine) throws GGEventsException;
	
	void applyConfiguration() throws GGEventsException;

}
