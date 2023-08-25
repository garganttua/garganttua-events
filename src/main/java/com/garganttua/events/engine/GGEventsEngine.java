package com.garganttua.events.engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.interfaces.IGGEventsAssetInfos;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;

public class GGEventsEngine implements IGGEventsEngine {

	private String assetId;
	private Map<String, Map<String, IGGEventsContext>> contexts;
	private IGGEventsEventHandler eventsCoreEventHandler;
	private ExecutorService executorService;
	private ScheduledExecutorService scheduledExecutorService;
	private List<String> packages;

	public GGEventsEngine(String assetId, Map<String, Map<String, IGGEventsContext>> contexts,
			IGGEventsEventHandler eventsCoreEventHandler, ExecutorService executorService,
			ScheduledExecutorService scheduledExecutorService, List<String> packages) {
				this.assetId = assetId;
				this.contexts = contexts;
				this.eventsCoreEventHandler = eventsCoreEventHandler;
				this.executorService = executorService;
				this.scheduledExecutorService = scheduledExecutorService;
				this.packages = packages;
	}

	@Override
	public IGGEventsEngine start() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsEngine stop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsEngine reload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGGEventsAssetInfos getAssetInfos() {
		// TODO Auto-generated method stub
		return null;
	}

}
