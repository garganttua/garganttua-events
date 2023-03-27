/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;

import java.util.concurrent.ExecutorService;

import com.gtech.garganttua.core.context.GGContextSubscription;
import com.gtech.garganttua.core.spec.exceptions.GGConnectorException;

public interface IGGConnector extends IGGMessageHandler, IGGConfigurable, IGGDescribable {
	
	void setPoolExecutor(ExecutorService poolExecutor);
	
	void setName(String name); 

	String getName();
	
	void start() throws GGConnectorException;

	void stop() throws GGConnectorException;

	void registerConsumer(GGContextSubscription subscription, IGGMessageHandler messageHandler, String tenantId, String clusterId, String assetId);

	void registerProducer(GGContextSubscription subscription, String tenantId, String clusterId, String assetId);
	
}
