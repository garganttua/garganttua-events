/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spring;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.garganttua.events.engine.GGEventsContextBuilder;
import com.garganttua.events.engine.GGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsContextBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsContextSourceConfigurationRegistry;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistry;
import com.garganttua.events.spec.objects.GGEventsContextSourceConfiguration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Service
public class GGEventsContextEngineSpringBean {
	
	@Value("${garganttua.events.assetId:1}")
	private String assetId;
	@Value("${garganttua.events.tenantId:1}")
	private String tenantId;
	@Value("${garganttua.events.clusterId:1}")
	private String clusterId;
	@Value("${garganttua.events.assetName}")
	private String assetName;
	@Value("${garganttua.events.assetVersion:0.0.1-SNAPSHOT}")
	private String assetVersion;
	
	@Getter
	private IGGEventsEngine contextEngine = new GGEventsEngine();
	
	@Getter
	private IGGEventsContextBuilder contextBuilder = new GGEventsContextBuilder();
	
	@Inject
	@Getter
	private IGGEventsContextSourceConfigurationRegistry configRegistry;
	
	@Inject
	@Getter
	private List<IGGEventsObjectRegistry> objectsRegistries;
	
	@Inject
	@Getter
	private List<GGEventsContextSourceConfiguration> contextSourcesConfigurations;
	
	@Inject
	@Qualifier(value="packages")
	private String[] packages;
	
	@Inject
	@Getter
	private ExecutorService executorService;
	
	@Inject
	@Getter
	private ScheduledExecutorService scheduledExecutorService;
	
	@Inject
	@Getter
	private IGGEventsEventHandler eventHandler;
	
	@Bean
	@Qualifier(value="GGEventsEngine")
	public IGGEventsEngine getEngine() {
		return this.contextEngine;
	}
	
	@PostConstruct
	public void init() {
		this.objectsRegistries.forEach(c -> {
			this.contextEngine.getObjectRegistries().addObjectRegistry(c.getLabel(), c);
		});
	
		this.contextSourcesConfigurations.forEach(c -> {
			this.configRegistry.registerContextSourceConfiguration(c);
		});
		
		this.contextEngine.registerContextSourceConfiguratorRegistry(this.configRegistry);
		this.contextEngine.registerEventHandler(this.eventHandler);
		this.contextEngine.init(this.assetId, this.contextBuilder, this.packages, this.executorService, this.scheduledExecutorService, this.assetName, this.assetVersion);
		this.contextEngine.start();
	}

//	@Override
//	public void start(GGServerServiceCommandRight right) throws GGServerServiceException{
//		log.info("Starting Garganttua Core Engine.");
//		this.status = GGServerServiceStatus.starting;
//		this.contextEngine.start();
//		this.status = GGServerServiceStatus.running;
//		log.info("Garganttua Core Engine started");
//	}

//	@Override
//	public void stop(GGServerServiceCommandRight right) throws GGServerServiceException{
//		log.info("Stopping Garganttua Core Engine.");
//		this.status = GGServerServiceStatus.stopping;
//		this.contextEngine.stop();
//		this.status = GGServerServiceStatus.stopped;
//		log.info("Garganttua Core Engine stopped");
//	}

//	@Override
//	public void init(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
//		log.info("Garganttua Core Context Engine Initialisation");
//		this.status = GGServerServiceStatus.initializing;
//		this.contextEngine.getObjectRegistries().addObjectRegistry("bean", new GGEventsSpringBeanRegistry(this.context));
//		
//		List<String> contexts = new ArrayList<String>();
//		boolean contextArgument = false; 
//		for( String arg: arguments ) {
//			if( contextArgument ) {
//				String[] splits = arg.split(",");
//				
//				for( String context: splits ) {
//					if( context.endsWith("ggc")) {
//						contexts.add(context);
//					}
//				}
//				break;
//			}
//			if( arg.equals(GGServerApplicationEngine.GARGANTTUA_SERVER_ARGUMENT_CONFIGURATIONS) ) {
//				contextArgument = true;
//			}
//		}
//
//		String[] contextFiles = new String[contexts.size()];
//		
//		int i = 0;
//		
//		log.info("Initializing Garganttua Core Context Engine with contexts :");
//		for(String context: contexts) {
//			log.info(" -> "+context);
//			contextFiles[i] = context;
//			i++;
//		}
//		
//		this.configRegistry.registerContextSourceConfiguration(new GGEventsContextSourceConfiguration("GGEventsContextFileSource", contextFiles));
//		this.contextSourcesConfigurations.forEach(c -> {
//			this.configRegistry.registerContextSourceConfiguration(c);
//		});
//				
//		this.contextEngine.registerContextSourceConfiguratorRegistry(this.configRegistry);
//		this.contextEngine.init(this.assetId, this.contextBuilder, this.packages, this.executorService, this.scheduledExecutorService, this.assetName, this.assetVersion);
//		log.info("Garganttua Core Context Engine Initialized");
//		this.status = GGServerServiceStatus.initialized;
//	}

//	@Override
//	public String getName() {
//		return "garganttua-core";
//	}

//	@Override
//	public GGServerServiceStatus getStatus() {
//		return this.status;
//	}
//	
//	@Override
//	public void flush(GGServerServiceCommandRight right) throws GGServerServiceException {
//		this.status = GGServerServiceStatus.flushing;
//		this.contextBuilder.flush();
//		this.status = GGServerServiceStatus.flushed;
//	}
	
//	@Override
//	public GGServerServicePriority getPriority() {
//		return GGServerServicePriority.medium;
//	}

//	@Override
//	public void restart(GGServerServiceCommandRight right, String[] arguments) throws GGServerServiceException {
//		this.stop(right);
//		this.flush(right);
//		this.init(right, arguments);
//		this.start(right);
//	}
}
