/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.gtech.garganttua.core.context.GGContextDataFlow;
import com.gtech.garganttua.core.context.GGContextSubscription;
import com.gtech.garganttua.core.context.GGContextTimeInterval;
import com.gtech.garganttua.core.context.GGContextTopic;
import com.gtech.garganttua.core.engine.consumers.GGTimeIntervalConsumer;
import com.gtech.garganttua.core.spec.exceptions.GGConnectorException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

class OnTimeIntervalConsumer {

	@Test
	void test() throws GGCoreException, InterruptedException, GGCoreProcessingException {

		GGContextDataFlow cdataflow = new GGContextDataFlow(null, null, null, false, null, false, null);
		GGDataflow dataflow = new GGDataflow(cdataflow);
		GGContextTopic ctopic = new GGContextTopic(null, null);
		GGTopic topic = new GGTopic(ctopic);
		
		IGGConnector connector = new IGGConnector() {

			@Override
			public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setPoolExecutor(ExecutorService poolExecutor) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void start() throws GGConnectorException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void stop() throws GGConnectorException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
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
			public void applyConfiguration() throws GGCoreException {
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
			public void registerConsumer(GGContextSubscription subscription, IGGMessageHandler messageHandler,
					String tenantId, String clusterId, String assetId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void registerProducer(GGContextSubscription subscription, String tenantId, String clusterId,
					String assetId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public GGContextObjDescriptor getDescriptor() {
				// TODO Auto-generated method stub
				return null;
			}

		};
		
		GGContextSubscription subscritpion = new GGContextSubscription("1", "/test", "test", null, false, false, new GGContextTimeInterval(5, TimeUnit.SECONDS), null, null, null);
		GGSubscription sub = new GGSubscription(dataflow, subscritpion, connector, topic, null, null);

		ScheduledExecutorService exec = new ScheduledThreadPoolExecutor(10);
		ExecutorService sexec = exec;
		
		GGTimeIntervalConsumer c = new GGTimeIntervalConsumer(null, null, null);
		
		GGExchange ex = GGExchange.emptyExchange("", "", "", new String("coucou").getBytes());
		
		c.handle(ex);
		
		synchronized (this) {
			this.wait(20000);
		}

	}

}
