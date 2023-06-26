/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.objects.GGEventsAssetContext;

public interface IGGEventsContextEngine {

	void init(String assetId, IGGEventsContextBuilder contextBuilder, String[] scanPackages, ExecutorService executorService, ScheduledExecutorService sExecutor, String assetName, String assetVersion);

	void start();

	void stop();

	void reloadContext();

	List<IGGEventsConnector> getConnectors();
	
	void registerContextSourceConfiguratorRegistry(IGGEventsContextSourceConfigurationRegistry contextSourceConfigurationRegistry);

	IGGEventsConnector getConnector(String tenantid, String clusterid, String name);

	IGGEventsSubscription getSubscription(String subscriptionId, String tenantId, String clusterId);

	ExecutorService getExecutorService();
	
	IGGEventsObjectRegistryHub getObjectRegistries();

	void registerEventHandler(IGGEventsCoreEventHandler handler);

	GGEventsAssetContext getAssetContext();
	
}
