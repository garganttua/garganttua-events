/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spring;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.garganttua.events.engine.GGEventsBuilder;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.reflection.annotation.scanner.GGSpringAnnotationScanner;
import com.garganttua.reflection.utils.GGObjectReflectionHelper;

import lombok.Getter;

@Service
public class GGEventsEngineSpringBean implements IGGEventsEngineSpringBean {
	
	static {
		GGObjectReflectionHelper.annotationScanner = new GGSpringAnnotationScanner();
	}
	
	@Value("${com.garganttua.events.assetId:1}")
	private String assetId;
	@Value("${com.garganttua.events.assetName}")
	private String assetName;
	@Value("${com.garganttua.events.assetVersion:0.0.1-SNAPSHOT}")
	private String assetVersion;
	@Value("${com.garganttua.events.threadPoolSize:10}")
	private int poolSize = 10;
	
	@Autowired
	private Optional<List<IGGEventsContextSource>> contextSources;
	
	@Autowired
	@Qualifier(value = "packages")
	private Optional<List<String>> packages;
	
	@Getter
	private IGGEventsEngine engine;
	
	@Autowired 
	private ApplicationContext springApplicationContext;

	@Override
	public void start() throws GGEventsException {
		this.engine.start();
	}
	
	@Override
	public void stop() throws GGEventsException {
		this.engine.stop();
	}
	
	@Override
	public void reload() throws GGEventsException {
		this.engine.reload();
	}

	@Override
	public void init() throws GGEventsException {
		IGGEventsBuilder builder = GGEventsBuilder.builder(this.assetId);
		
		builder.registry(GGEventsSpringBeanRegistry.LABEL, new GGEventsSpringBeanRegistry(this.springApplicationContext));
		
		builder.lookup("com.garganttua");
		
		this.packages.ifPresent(list -> {
			list.forEach( package_ -> {
				builder.lookup(package_);
			});
		});

		this.contextSources.ifPresent(list -> {
			list.forEach( source -> {
				builder.source(source);
			});
		});
		
		builder.maxThreadPoolSize(10);
		builder.threadPoolKeepAliveTime(30);
		builder.threadPoolKeepAliveTimeUnit(TimeUnit.SECONDS);
		this.engine = builder.build();
		this.engine.init();
	}

	@Override
	public void flush() throws GGEventsException {
		this.engine.flush();
	}
}
