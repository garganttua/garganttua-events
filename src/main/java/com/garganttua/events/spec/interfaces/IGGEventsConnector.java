/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import java.util.concurrent.ExecutorService;

import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.spec.exceptions.GGEventsConnectorException;

public interface IGGEventsConnector extends IGGEventsMessageHandler, IGGEventsConfigurable, IGGEventsDescribable, IGGEventsNamable {
	
	void setPoolExecutor(ExecutorService poolExecutor);
	
	void start() throws GGEventsConnectorException;

	void stop() throws GGEventsConnectorException;

	void registerConsumer(GGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler, String tenantId, String clusterId, String assetId);

	void registerProducer(GGEventsContextSubscription subscription, String tenantId, String clusterId, String assetId);
	
}
