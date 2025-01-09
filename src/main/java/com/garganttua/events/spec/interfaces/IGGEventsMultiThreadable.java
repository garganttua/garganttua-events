package com.garganttua.events.spec.interfaces;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface IGGEventsMultiThreadable {

	void setExecutorService(ExecutorService service);
	
	void setScheduledExecutorService(ScheduledExecutorService service);
	
}
