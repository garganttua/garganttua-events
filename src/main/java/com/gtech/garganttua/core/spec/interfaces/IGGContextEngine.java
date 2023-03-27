/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.gtech.garganttua.core.spec.objects.GGAssetContext;

public interface IGGContextEngine {

	void init(String assetId, IGGContextBuilder contextBuilder, String[] scanPackages, ExecutorService executorService, ScheduledExecutorService sExecutor, String assetName, String assetVersion);

	void start();

	void stop();

	void reloadContext();

	List<IGGConnector> getConnectors();
	
	void registerContextSourceConfiguratorRegistry(IGGContextSourceConfigurationRegistry contextSourceConfigurationRegistry);

	IGGConnector getConnector(String tenantid, String clusterid, String name);

	IGGSubscription getSubscription(String subscriptionId, String tenantId, String clusterId);

	ExecutorService getExecutorService();
	
	IGGObjectRegistryHub getObjectRegistries();

	void registerEventHandler(IGGCoreEventHandler handler);

	GGAssetContext getAssetContext();
	
}
