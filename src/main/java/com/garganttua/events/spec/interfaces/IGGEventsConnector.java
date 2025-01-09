/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import java.util.concurrent.ExecutorService;

import com.garganttua.events.spec.exceptions.GGEventsConnectorException;
import com.garganttua.events.spec.objects.GGEventsConnectorConsumerRegistrationRequest;
import com.garganttua.events.spec.objects.GGEventsConnectorProducerRegistrationRequest;

public interface IGGEventsConnector extends IGGEventsMessageHandler, IGGEventsConfigurable, IGGEventsDescribable, IGGEventsNamable {
	
	void setPoolExecutor(ExecutorService poolExecutor);
	
	void start() throws GGEventsConnectorException;

	void stop() throws GGEventsConnectorException;

	void registerConsumer(GGEventsConnectorConsumerRegistrationRequest request);

	void registerProducer(GGEventsConnectorProducerRegistrationRequest request);
	
}
