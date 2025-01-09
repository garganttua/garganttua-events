package com.garganttua.events.spec.interfaces.context;

import java.util.concurrent.TimeUnit;

public interface IGGEventsContextTimeInterval {

	TimeUnit getTimeUnit();

	long getInterval();

}
