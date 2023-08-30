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
import com.garganttua.events.context.GGEventsContextItemSource;
import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.annotations.GGEventsDistributedLock;
import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.IGGEventsDistributedLock;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextMergeableItem;
import com.garganttua.events.spec.objects.GGEventsUtils;

public class GGEventsBuilder implements IGGEventsBuilder {

	private String assetId;
	
	private BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
	private TimeUnit threadPoolKeepAliveTimeUnit = TimeUnit.SECONDS;
	private long threadPoolKeepAliveTime = 10;
	private int threadPoolSize = 100;
	private int maxThreadPoolSize = 200;
	
	//Flushable fields
	private ScheduledExecutorService scheduledExecutorService;
	private List<IGGEventsEventHandler> eventsHandler = new ArrayList<IGGEventsEventHandler>();
	private ExecutorService executorService;
	private Map<String, Map<String, IGGEventsContext>> contexts;
	private List<String> packages;
	private Map<String, Map<String, Class<?>>> connectors = new HashMap<String, Map<String, Class<?>>>();
	private Map<String, Map<String, Class<?>>> locks = new HashMap<String, Map<String, Class<?>>>();
	private Map<String, Map<String, Class<?>>> processors = new HashMap<String, Map<String, Class<?>>>();
	private Map<String, Map<String, Class<?>>> sources = new HashMap<String, Map<String, Class<?>>>();

	private GGEventsBuilder(String assetId) {
		this.assetId = assetId;
		this.contexts = new HashMap<String, Map<String,IGGEventsContext>>();
		this.packages = new ArrayList<String>();
	}

	public static IGGEventsBuilder builder(String assetId) {
		return new GGEventsBuilder(assetId).lookup("com.garganttua");
	}

	@Override
	public IGGEventsContext context(String tenantId, String clusterId) {
		
		IGGEventsContext clusterContext = null;
		Map<String, IGGEventsContext> tenantContexts = this.contexts.get(tenantId);
		
		if( tenantContexts == null ) {
			tenantContexts = new HashMap<String, IGGEventsContext>();
			this.contexts.put(tenantId, tenantContexts);
		}
		
		clusterContext = tenantContexts.get(clusterId);
		
		if( clusterContext == null) {
			clusterContext = new GGEventsContext(this.assetId, tenantId, clusterId);
			((GGEventsContext) clusterContext).builder(this);
			tenantContexts.put(clusterId, clusterContext);
		}
		((GGEventsContext) clusterContext).setSourcesObjects(this.sources);
		return clusterContext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IGGEventsContext context(IGGEventsContext context) {
		IGGEventsContext contextAlreadyExisting = this.context(context.getTenantId(), context.getClusterId());
		((IGGEventsContextMergeableItem<IGGEventsContext>) contextAlreadyExisting).merge(context);
		return contextAlreadyExisting;
	}

	@Override
	public IGGEventsBuilder source(String type, String version, String configuration) {
		try {
			IGGEventsContextSource source = GGEventsUtils.getSourceObj(type, version, this.sources);
			IGGEventsContext readContext = source.readContext(configuration);
			((GGEventsContext) readContext).setAssetId(this.assetId);
			readContext.source(new GGEventsContextItemSource(this.assetId, readContext.getClusterId(), type+"://"+configuration));
			
			this.context(readContext);
		} catch (GGEventsException e) {
			throw new IllegalArgumentException(e);
		}
		
		return this;
	}

	@Override
	public IGGEventsBuilder source(IGGEventsContextSource source) {
		try {
			IGGEventsContext context = source.readContext();
			context.source(new GGEventsContextItemSource(this.assetId, context.getClusterId(), source.getType()+"://"+source.getConfiguration()));
			((GGEventsContext) context).setAssetId(this.assetId);
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

		IGGEventsEngine engine = new GGEventsEngine(this.assetId, this.contexts, this.eventsHandler, this.executorService, this.scheduledExecutorService, this.connectors, this.locks, this.processors);
		
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
	public IGGEventsBuilder eventHanlder(IGGEventsEventHandler eventsHandler) {
		this.eventsHandler.add(eventsHandler);
		return this;
	}

	@Override
	public IGGEventsBuilder flush() {
		this.contexts.clear();
		this.executorService = null;
		this.scheduledExecutorService = null;
		this.eventsHandler.clear();
		this.packages.clear();
		return this;
	}

	@Override
	public IGGEventsBuilder lookup(String packageName) {
		this.packages.add(packageName);
		try {
			GGEventsContextAnnotateClassesRegistry.findClassesWithAnnotationAndInterface(packageName, GGEventsConnector.class, IGGEventsConnector.class, this.connectors);
			GGEventsContextAnnotateClassesRegistry.findClassesWithAnnotationAndInterface(packageName, GGEventsDistributedLock.class, IGGEventsDistributedLock.class, this.locks);
			GGEventsContextAnnotateClassesRegistry.findClassesWithAnnotationAndInterface(packageName, GGEventsProcessor.class, IGGEventsProcessor.class, this.processors);
			GGEventsContextAnnotateClassesRegistry.findClassesWithAnnotationAndInterface(packageName, GGEventsContextSource.class, IGGEventsContextSource.class, this.sources);
		} catch (GGEventsException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

}
