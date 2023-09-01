/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import java.util.Map;

import com.garganttua.events.engine.GGEventsDataflow;
import com.garganttua.events.engine.GGEventsRoute;
import com.garganttua.events.engine.GGEventsSubscription;
import com.garganttua.events.engine.GGEventsTopic;

public interface IGGEventsEngine {

	IGGEventsEngine start();

	IGGEventsEngine stop();

	IGGEventsEngine reload();

	IGGEventsAssetInfos getAssetInfos();
	
	Map<String, Map<String, Map<String, IGGEventsConnector>>> getConnectors();
	Map<String, Map<String, Map<String, GGEventsTopic>>> getTopics();
	Map<String, Map<String, Map<String, GGEventsDataflow>>> getDataflows();
	Map<String, Map<String, Map<String, GGEventsSubscription>>> getSubscriptions();
	Map<String, Map<String, Map<String, GGEventsRoute>>> getRoutes();
	Map<String, Map<String, Map<String, IGGEventsDistributedLock>>> getLocks();

}
