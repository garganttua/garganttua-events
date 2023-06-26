package com.garganttua.events.spec.interfaces;

import com.garganttua.events.spec.exceptions.GGEventsCoreException;

public interface IGGEventsConfigurable {

	String getConfiguration();

	void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) throws GGEventsCoreException;
	
	void applyConfiguration() throws GGEventsCoreException;

}
