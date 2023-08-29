/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.garganttua.events.context.GGEventsContextDataflow;
import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.context.GGEventsContextTimeInterval;
import com.garganttua.events.context.GGEventsContextTopic;
import com.garganttua.events.engine.consumers.GGEventsTimeIntervalConsumer;
import com.garganttua.events.spec.exceptions.GGEventsConnectorException;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

class OnTimeIntervalConsumer {

	@Test
	void test() throws GGEventsException, InterruptedException, GGEventsProcessingException {

		GGEventsContextDataflow cdataflow = new GGEventsContextDataflow(null, null, null, false, null, false, null);
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
			public void handle(GGEventsExchange exchange) throws GGEventsProcessingException, GGEventsException {
				System.out.println(new String(exchange.getValue()));
			}

			@Override
			public String getConfiguration() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void applyConfiguration() throws GGEventsException {
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
			public void registerConsumer(IGGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler,
					String tenantId, String clusterId, String assetId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void registerProducer(IGGEventsContextSubscription subscription, String tenantId, String clusterId,
					String assetId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public GGEventsContextObjDescriptor getDescriptor() {
				// TODO Auto-generated method stub
				return null;
			}

		};
		
		GGEventsContextSubscription subscritpion = new GGEventsContextSubscription("1", "/test", "test", null, new GGEventsContextTimeInterval(5, TimeUnit.SECONDS), null, null, null);
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
