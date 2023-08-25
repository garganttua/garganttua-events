package com.garganttua.events.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextMergeableItem;

public class GGEventsBuilder implements IGGEventsBuilder {

	private String assetId;
	private BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
	private TimeUnit threadPoolKeepAliveTimeUnit = TimeUnit.SECONDS;
	private long threadPoolKeepAliveTime = 10;
	private int threadPoolSize = 100;
	private int maxThreadPoolSize = 200;
	
	//Flushable fields
	private ScheduledExecutorService scheduledExecutorService;
	private IGGEventsEventHandler eventsCoreEventHandler;
	private ExecutorService executorService;
	private Map<String, Map<String, IGGEventsContext>> contexts;
	private List<String> packages;

	private GGEventsBuilder(String assetId) {
		this.assetId = assetId;
		this.contexts = new HashMap<String, Map<String,IGGEventsContext>>();
		this.packages = new ArrayList<String>();
	}

	public static IGGEventsBuilder builder(String assetId) {
		return new GGEventsBuilder(assetId);
	}

	@Override
	public IGGEventsContext context(String tenantId, String clusterId) {
		
		IGGEventsContext clusterContext = null;
		Map<String, IGGEventsContext> tenantContexts = this.contexts.get(tenantId);
		
		if( tenantContexts == null ) {
			tenantContexts = new HashMap<String, IGGEventsContext>();
		}
		
		clusterContext = tenantContexts.get(clusterId);
		
		if( clusterContext == null) {
			clusterContext = new GGEventsContext(tenantId, clusterId);
			((GGEventsContext) clusterContext).builder(this);
			tenantContexts.put(clusterId, clusterContext);
		}
		this.contexts.put(tenantId, tenantContexts);
		return clusterContext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IGGEventsContext context(IGGEventsContext context) {
		var contextAlreadyExisting = this.context(context.getTenantId(), context.getClusterId());
		((IGGEventsContextMergeableItem<IGGEventsContext>) contextAlreadyExisting).merge(context);
		return contextAlreadyExisting;
	}

	@Override
	public IGGEventsBuilder source(String type, String configuration) {
		Map<String, IGGEventsContextSource> sources = new HashMap<String, IGGEventsContextSource>();
		this.packages.forEach(packageName -> {
			try {
				GGEventsContextSourcesRegistry.findAvailableSources(packageName).forEach((name, source) -> {
					sources.put(name, source);
				});
			} catch (GGEventsException e) {
				throw new IllegalArgumentException(e);
			}
		});
		
		IGGEventsContextSource contextSource = sources.get(type);
		if( contextSource == null ) {
			throw new IllegalArgumentException("Source "+type+" is not found");
		}
		
		try {
			this.context(contextSource.readContext(configuration));
		} catch (GGEventsException e) {
			throw new IllegalArgumentException(e);
		}
		
		return this;
	}

	@Override
	public IGGEventsBuilder source(IGGEventsContextSource source) {
		try {
			GGEventsContext context = source.readContext();
			this.context(context);
		} catch (GGEventsException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	@Override
	public IGGEventsEngine build() {
		if( this.executorService == null )
			this.executorService = new ThreadPoolExecutor(this.threadPoolSize/2, this.maxThreadPoolSize/2, this.threadPoolKeepAliveTime, this.threadPoolKeepAliveTimeUnit, this.workQueue);
		if( this.scheduledExecutorService == null )
			this.scheduledExecutorService = new ScheduledThreadPoolExecutor(this.maxThreadPoolSize/2);

		IGGEventsEngine engine = new GGEventsEngine(this.assetId, this.contexts, this.eventsCoreEventHandler, this.executorService, this.scheduledExecutorService, this.packages);
		
		return engine;
	}

	@Override
	public IGGEventsBuilder executorService(ExecutorService executorService) {
		this.executorService = executorService;
		return this;
	}

	@Override
	public IGGEventsBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
		this.scheduledExecutorService = scheduledExecutorService;
		return this;
	}

	@Override
	public IGGEventsBuilder threadPoolKeepAliveTimeUnit(TimeUnit threadPoolKeepAliveTimeUnit) {
		this.threadPoolKeepAliveTimeUnit = threadPoolKeepAliveTimeUnit;
		return this;
	}

	@Override
	public IGGEventsBuilder threadPoolKeepAliveTime(long threadPoolKeepAliveTime) {
		this.threadPoolKeepAliveTime = threadPoolKeepAliveTime;
		return this;
	}

	@Override
	public IGGEventsBuilder threadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
		return this;
	}

	@Override
	public IGGEventsBuilder maxThreadPoolSize(int maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
		return this;
	}

	@Override
	public IGGEventsBuilder eventHanlder(IGGEventsEventHandler eventsCoreEventHandler) {
		this.eventsCoreEventHandler = eventsCoreEventHandler;
		return this;
	}

	@Override
	public IGGEventsBuilder flush() {
		this.contexts = new HashMap<String, Map<String,IGGEventsContext>>();
		this.executorService = null;
		this.scheduledExecutorService = null;
		this.eventsCoreEventHandler = null;
		this.packages = new ArrayList<String>();
		return this;
	}

	@Override
	public IGGEventsBuilder lookup(String packageName) {
		this.packages.add(packageName);
		return this;
	}

}
