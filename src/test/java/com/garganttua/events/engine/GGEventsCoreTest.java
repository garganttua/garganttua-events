/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.Test;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.context.GGEventsContextConnector;
import com.garganttua.events.context.GGEventsContextConsumerConfiguration;
import com.garganttua.events.context.GGEventsContextDataFlow;
import com.garganttua.events.context.GGEventsContextProcessor;
import com.garganttua.events.context.GGEventsContextProducerConfiguration;
import com.garganttua.events.context.GGEventsContextPublicationMode;
import com.garganttua.events.context.GGEventsContextRoute;
import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.context.GGEventsContextTopic;
import com.garganttua.events.engine.GGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsContextSourceConfigurationRegistry;
import com.garganttua.events.spec.objects.GGEventsContextSourceConfiguration;

import ch.qos.logback.core.util.ExecutorServiceUtil;

public class GGEventsTest {
	
	public static GGEventsContext context = new GGEventsContext();
	Map<String, GGEventsContextProcessor> processors = new HashMap<String, GGEventsContextProcessor>();
	Map<Integer, GGEventsContextProcessor> processors1 = new HashMap<Integer, GGEventsContextProcessor>();
	List<GGEventsContextTopic> topics = new ArrayList<GGEventsContextTopic>();
	List<GGEventsContextDataFlow> dataflows = new ArrayList<GGEventsContextDataFlow>();
	List<GGEventsContextSubscription> subscriptions = new ArrayList<GGEventsContextSubscription>();
	List<GGEventsContextRoute> routes = new ArrayList<GGEventsContextRoute>();
	
	@Test
	public void test() throws Exception {
		
		GGEventsContextProcessor prco = new GGEventsContextProcessor("log", "1.0.0", "level=WARN&withMetaData=true", "sddsd");
		GGEventsContextProcessor prco1 = new GGEventsContextProcessor("log", "1.0.0", "level=WARN&withMetaData=true", "54");
		GGEventsContextProcessor prco2 = new GGEventsContextProcessor("log", "1.0.0", "level=WARN&withMetaData=true", "8644");
		
		this.processors.put(prco.getUuid(), prco);
		this.processors.put(prco1.getUuid(), prco1);
		this.processors.put(prco2.getUuid(), prco2);
		
		this.processors1.put(1, prco);
		this.processors1.put(2, prco1);
		this.processors1.put(3, prco2);
		
		context.setClusterId("1");
		context.setTenantId("1");
		
		this.context.setConnectors(new ArrayList<GGEventsContextConnector>());
		this.context.getConnectors().add(new GGEventsContextConnector("test", "TestConnector", "config=12", null, "1.0"));
		
		this.topics.add(new GGEventsContextTopic("/entry", null));
		this.topics.add(new GGEventsContextTopic("/exit", null));
		
		this.context.setTopics(this.topics);
		
		this.dataflows.add(new GGEventsContextDataFlow("1122", "test", "test", true, "1.0", true, null));
	
		this.context.setDataflows(this.dataflows);
		
		this.subscriptions.add(new GGEventsContextSubscription("1122", "/entry", "test", GGEventsContextPublicationMode.ON_CHANGE, null, new GGEventsContextConsumerConfiguration(), new GGEventsContextProducerConfiguration(), null));
		this.subscriptions.add(new GGEventsContextSubscription("1122", "/exit", "test", GGEventsContextPublicationMode.ON_CHANGE, null, new GGEventsContextConsumerConfiguration(), new GGEventsContextProducerConfiguration(), null));
		 
		this.context.setSubscriptions(this.subscriptions);
		
		this.routes.add(new GGEventsContextRoute("12122", "test://1122/entry", this.processors1, "test://1122/exit", null, null, null));
		
		this.context.setRoutes(this.routes);
		
		GGEventsEngine fwk = new GGEventsEngine();
		
		fwk.registerContextSourceConfiguratorRegistry(new IGGEventsContextSourceConfigurationRegistry() {
			
			@Override
			public String[] getContextSourceConfiguration(String name) {
				return new String[1];
			}

			@Override
			public void registerContextSourceConfiguration(GGEventsContextSourceConfiguration configuration) {
				// TODO Auto-generated method stub
				
			}
		});
		
		String[] packs = {"com.gtech"};
		ExecutorService exec = ExecutorServiceUtil.newExecutorService();
		ScheduledExecutorService exece = ExecutorServiceUtil.newScheduledExecutorService();
		fwk.init(null, new GGEventsContextBuilder(), packs, exec, exece , null, null);
		fwk.start();
		
		List<IGGEventsConnector> connectors = fwk.getConnectors();
		
		ConnectorTest con = (ConnectorTest) connectors.get(0);
		
		String message = "coucou :)"; 

 		con.receivedMessage( message.getBytes() );
		
	}
	
}
