/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.garganttua.events.spec.interfaces.context.IGGEventsContext;

public interface IGGEventsBuilder {
	
	IGGEventsBuilder lookup(String packageName);
	
	IGGEventsBuilder flush();

	IGGEventsContext context(String tenantId, String clusterId);
	
	IGGEventsContext context(IGGEventsContext context);

	IGGEventsBuilder source(String type, String version, String configuration);

	IGGEventsBuilder source(IGGEventsContextSource source);

	IGGEventsBuilder executorService(ExecutorService executorService);

	IGGEventsBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService);

	IGGEventsBuilder threadPoolKeepAliveTimeUnit(TimeUnit seconds);

	IGGEventsBuilder threadPoolKeepAliveTime(long threadPoolKeepAliveTime);

	IGGEventsBuilder threadPoolSize(int threadPoolSize);

	IGGEventsBuilder maxThreadPoolSize(int maxThreadPoolSize);

	IGGEventsEngine build();

	IGGEventsBuilder eventHanlder(IGGEventsEventHandler GGEventsEventHandler);

	IGGEventsBuilder registry(String label, IGGEventsObjectRegistry registry);
}
