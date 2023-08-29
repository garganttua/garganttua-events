package com.garganttua.events.spec.interfaces.context;

import com.garganttua.events.context.GGEventsContextDestinationPolicy;

public interface IGGEventsContextProducerConfiguration {

	String getDestinationUuid();

	GGEventsContextDestinationPolicy getDpolicy();

}
