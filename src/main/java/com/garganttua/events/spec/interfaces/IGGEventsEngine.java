/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


import java.util.List;

public interface IGGEventsEngine {

	IGGEventsEngine start();

	IGGEventsEngine stop();

	IGGEventsEngine reload();

	List<IGGEventsConnector> getConnectors();

	IGGEventsConnector getConnector(String tenantid, String clusterid, String name);

	IGGEventsSubscription getSubscription(String subscriptionId, String tenantId, String clusterId);

	IGGEventsAssetInfos getAssetInfos();

}
