/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.garganttua.events.context.GGEventsContextDataFlow;
import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.context.GGEventsContextTimeInterval;
import com.garganttua.events.context.GGEventsContextTopic;
import com.garganttua.events.engine.GGEventsDataflow;
import com.garganttua.events.engine.GGEventsSubscription;
import com.garganttua.events.engine.GGEventsTopic;
import com.garganttua.events.engine.consumers.GGEventsTimeIntervalConsumer;
import com.garganttua.events.spec.exceptions.GGEventsConnectorException;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

class OnTimeIntervalConsumer {

	@Test
	void test() throws GGEventsCoreException, InterruptedException, GGEventsCoreProcessingException {

		GGEventsContextDataFlow cdataflow = new GGEventsContextDataFlow(null, null, null, false, null, false, null);
		GGEventsDataflow dataflow = new GGEventsDataflow(cdataflow);
		GGEventsContextTopic ctopic = new GGEventsContextTopic(null, null);
		GGEventsTopic topic = new GGEventsTopic(ctopic);
		
		IGGEventsConnector connector = new IGGEventsConnector() {

			@Override
			public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setPoolExecutor(ExecutorService poolExecutor) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void start() throws GGEventsConnectorException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void stop() throws GGEventsConnectorException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
				System.out.println(new String(exchange.getValue()));
			}

			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return null;
			}


			@Override
			public String getConfiguration() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void applyConfiguration() throws GGEventsCoreException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setName(String name) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void registerConsumer(GGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler,
					String tenantId, String clusterId, String assetId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void registerProducer(GGEventsContextSubscription subscription, String tenantId, String clusterId,
					String assetId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public GGEventsContextObjDescriptor getDescriptor() {
				// TODO Auto-generated method stub
				return null;
			}

		};
		
		GGEventsContextSubscription subscritpion = new GGEventsContextSubscription("1", "/test", "test", null, false, false, new GGEventsContextTimeInterval(5, TimeUnit.SECONDS), null, null, null);
		GGEventsSubscription sub = new GGEventsSubscription(dataflow, subscritpion, connector, topic, null, null);

		ScheduledExecutorService exec = new ScheduledThreadPoolExecutor(10);
		ExecutorService sexec = exec;
		
		GGEventsTimeIntervalConsumer c = new GGEventsTimeIntervalConsumer(null, null, null);
		
		GGEventsExchange ex = GGEventsExchange.emptyExchange("", "", "", new String("coucou").getBytes());
		
		c.handle(ex);
		
		synchronized (this) {
			this.wait(20000);
		}

	}

}
