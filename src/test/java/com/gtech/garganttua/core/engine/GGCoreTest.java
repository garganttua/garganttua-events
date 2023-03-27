/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.jupiter.api.Test;

import com.gtech.garganttua.core.context.GGContext;
import com.gtech.garganttua.core.context.GGContextConnector;
import com.gtech.garganttua.core.context.GGContextConsumerConfiguration;
import com.gtech.garganttua.core.context.GGContextDataFlow;
import com.gtech.garganttua.core.context.GGContextProcessor;
import com.gtech.garganttua.core.context.GGContextProducerConfiguration;
import com.gtech.garganttua.core.context.GGContextPublicationMode;
import com.gtech.garganttua.core.context.GGContextRoute;
import com.gtech.garganttua.core.context.GGContextSubscription;
import com.gtech.garganttua.core.context.GGContextTopic;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGContextSourceConfigurationRegistry;
import com.gtech.garganttua.core.spec.objects.GGContextSourceConfiguration;

import ch.qos.logback.core.util.ExecutorServiceUtil;

public class GGCoreTest {
	
	public static GGContext context = new GGContext();
	Map<String, GGContextProcessor> processors = new HashMap<String, GGContextProcessor>();
	Map<Integer, GGContextProcessor> processors1 = new HashMap<Integer, GGContextProcessor>();
	List<GGContextTopic> topics = new ArrayList<GGContextTopic>();
	List<GGContextDataFlow> dataflows = new ArrayList<GGContextDataFlow>();
	List<GGContextSubscription> subscriptions = new ArrayList<GGContextSubscription>();
	List<GGContextRoute> routes = new ArrayList<GGContextRoute>();
	
	@Test
	public void test() throws Exception {
		
		GGContextProcessor prco = new GGContextProcessor("log", "1.0.0", "level=WARN&withMetaData=true", "sddsd");
		GGContextProcessor prco1 = new GGContextProcessor("log", "1.0.0", "level=WARN&withMetaData=true", "54");
		GGContextProcessor prco2 = new GGContextProcessor("log", "1.0.0", "level=WARN&withMetaData=true", "8644");
		
		this.processors.put(prco.getUuid(), prco);
		this.processors.put(prco1.getUuid(), prco1);
		this.processors.put(prco2.getUuid(), prco2);
		
		this.processors1.put(1, prco);
		this.processors1.put(2, prco1);
		this.processors1.put(3, prco2);
		
		context.setClusterId("1");
		context.setTenantId("1");
		
		this.context.setConnectors(new ArrayList<GGContextConnector>());
		this.context.getConnectors().add(new GGContextConnector("test", "TestConnector", "config=12", null, "1.0"));
		
		this.topics.add(new GGContextTopic("/entry", null));
		this.topics.add(new GGContextTopic("/exit", null));
		
		this.context.setTopics(this.topics);
		
		this.dataflows.add(new GGContextDataFlow("1122", "test", "test", true, "1.0", true, null));
	
		this.context.setDataflows(this.dataflows);
		
		this.subscriptions.add(new GGContextSubscription("1122", "/entry", "test", GGContextPublicationMode.ON_CHANGE, false, false, null, new GGContextConsumerConfiguration(), new GGContextProducerConfiguration(), null));
		this.subscriptions.add(new GGContextSubscription("1122", "/exit", "test", GGContextPublicationMode.ON_CHANGE, false, false, null, new GGContextConsumerConfiguration(), new GGContextProducerConfiguration(), null));
		 
		this.context.setSubscriptions(this.subscriptions);
		
		this.routes.add(new GGContextRoute("12122", "test://1122/entry", this.processors1, "test://1122/exit", null, null, null));
		
		this.context.setRoutes(this.routes);
		
		GGContextEngine fwk = new GGContextEngine();
		
		fwk.registerContextSourceConfiguratorRegistry(new IGGContextSourceConfigurationRegistry() {
			
			@Override
			public String[] getContextSourceConfiguration(String name) {
				return new String[1];
			}

			@Override
			public void registerContextSourceConfiguration(GGContextSourceConfiguration configuration) {
				// TODO Auto-generated method stub
				
			}
		});
		
		String[] packs = {"com.gtech"};
		ExecutorService exec = ExecutorServiceUtil.newExecutorService();
		ScheduledExecutorService exece = ExecutorServiceUtil.newScheduledExecutorService();
		fwk.init(null, new GGContextBuilder(), packs, exec, exece , null, null);
		fwk.start();
		
		List<IGGConnector> connectors = fwk.getConnectors();
		
		ConnectorTest con = (ConnectorTest) connectors.get(0);
		
		String message = "coucou :)"; 

 		con.receivedMessage( message.getBytes() );
		
	}
	
}
