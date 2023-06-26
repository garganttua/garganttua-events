/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.garganttua.events.engine.GGEventsContextBuilder;
import com.garganttua.events.engine.GGEventsContextEngine;
import com.garganttua.events.spec.interfaces.IGGEventsContextBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsContextEngine;
import com.garganttua.events.spec.interfaces.IGGEventsContextSourceConfigurationRegistry;
import com.garganttua.events.spec.interfaces.IGGEventsCoreEventHandler;
import com.garganttua.events.spec.objects.GGEventsContextSourceConfiguration;
import com.gtech.pegasus.core.execution.PGApplicationEngine;
import com.gtech.pegasus.core.services.IPGService;
import com.gtech.pegasus.core.services.PGServiceCommandRight;
import com.gtech.pegasus.core.services.PGServiceException;
import com.gtech.pegasus.core.services.PGServicePriority;
import com.gtech.pegasus.core.services.PGServiceStatus;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GGEventsCorePegasusService implements IPGService {
	
	@Value("${garganttua.backend.assetId:1}")
	private String assetId;
	@Value("${garganttua.backend.tenantId:1}")
	private String tenantId;
	@Value("${garganttua.backend.clusterId:1}")
	private String clusterId;
	@Value("${garganttua.backend.assetName}")
	private String assetName;
	@Value("${garganttua.backend.assetVersion:0.0.1-SNAPSHOT}")
	private String assetVersion;
	
	@Getter
	private IGGEventsContextEngine contextEngine;
	
	private IGGEventsContextBuilder contextBuilder = new GGEventsContextBuilder();
	
	@Inject
	private IGGEventsContextSourceConfigurationRegistry configRegistry;
	
	@Inject
	private List<GGEventsContextSourceConfiguration> contextSourcesConfigurations;
	
	@Bean
	private GGEventsContextSourceConfiguration fakeConfiguration() {
		String[] s = {""};
		return new GGEventsContextSourceConfiguration("fake", s);
	}
	
//	@Inject
//	@Qualifier(value="arguments")
//	private String[] arguments;
	
	@Inject
	@Qualifier(value="packages")
	private String[] packages;
	
	@Inject
	private ExecutorService executorService;
	
	@Inject
	private ScheduledExecutorService scheduledExecutorService;
	
	@Inject
	private ApplicationContext context;
	
	@Inject
	private SpringApplication application;
	
	private PGServiceStatus status = PGServiceStatus.flushed;
	
	@Bean
	@Qualifier(value="coreContextEngine")
	public IGGEventsContextEngine getEngine() {
		this.contextEngine = new GGEventsContextEngine();
		return this.contextEngine;
	};
	
	@Bean
	public IGGEventsCoreEventHandler eventHandler() {
		IGGEventsCoreEventHandler eventHandler = new GGEventsCoreEventHandler(this.application, this.context);
		this.contextEngine.registerEventHandler(eventHandler);
		return eventHandler;
	}	

	@Override
	public void start(PGServiceCommandRight right) throws PGServiceException{
		log.info("Starting Garganttua Core Engine.");
		this.status = PGServiceStatus.starting;
		this.contextEngine.start();
		this.status = PGServiceStatus.running;
		log.info("Garganttua Core Engine started");
	}

	@Override
	public void stop(PGServiceCommandRight right) throws PGServiceException{
		log.info("Stopping Garganttua Core Engine.");
		this.status = PGServiceStatus.stopping;
		this.contextEngine.stop();
		this.status = PGServiceStatus.stopped;
		log.info("Garganttua Core Engine stopped");
	}

	@Override
	public void init(PGServiceCommandRight right, String[] arguments) throws PGServiceException {
		log.info("Garganttua Core Context Engine Initialisation");
		this.status = PGServiceStatus.initializing;
		this.contextEngine.getObjectRegistries().addObjectRegistry("bean", new GGEventsSpringBeanRegistry(this.context));
		
		List<String> contexts = new ArrayList<String>();
		boolean contextArgument = false; 
		for( String arg: arguments ) {
			if( contextArgument ) {
				String[] splits = arg.split(",");
				
				for( String context: splits ) {
					if( context.endsWith("ggc")) {
						contexts.add(context);
					}
				}
				break;
			}
			if( arg.equals(PGApplicationEngine.PEGASUS_ARGUMENT_CONFIGURATIONS) ) {
				contextArgument = true;
			}
		}

		String[] contextFiles = new String[contexts.size()];
		
		int i = 0;
		
		log.info("Initializing Garganttua Core Context Engine with contexts :");
		for(String context: contexts) {
			log.info(" -> "+context);
			contextFiles[i] = context;
			i++;
		}
		
		this.configRegistry.registerContextSourceConfiguration(new GGEventsContextSourceConfiguration("GGEventsContextFileSource", contextFiles));
		this.contextSourcesConfigurations.forEach(c -> {
			this.configRegistry.registerContextSourceConfiguration(c);
		});
				
		this.contextEngine.registerContextSourceConfiguratorRegistry(this.configRegistry);
		this.contextEngine.init(this.assetId, this.contextBuilder, this.packages, this.executorService, this.scheduledExecutorService, this.assetName, this.assetVersion);
		log.info("Garganttua Core Context Engine Initialized");
		this.status = PGServiceStatus.initialized;
	}

	@Override
	public String getName() {
		return "garganttua-core";
	}

	@Override
	public PGServiceStatus getStatus() {
		return this.status;
	}
	
	@Override
	public void flush(PGServiceCommandRight right) throws PGServiceException {
		this.status = PGServiceStatus.flushing;
		this.contextBuilder.flush();
		this.status = PGServiceStatus.flushed;
	}
	
	@Override
	public PGServicePriority getPriority() {
		return PGServicePriority.medium;
	}

	@Override
	public void restart(PGServiceCommandRight right, String[] arguments) throws PGServiceException {
		this.stop(right);
		this.flush(right);
		this.init(right, arguments);
		this.start(right);
	}
}
