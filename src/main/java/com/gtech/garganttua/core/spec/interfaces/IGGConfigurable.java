package com.gtech.garganttua.core.spec.interfaces;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;

public interface IGGConfigurable {

	String getConfiguration();

	void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) throws GGCoreException;
	
	void applyConfiguration() throws GGCoreException;

}
