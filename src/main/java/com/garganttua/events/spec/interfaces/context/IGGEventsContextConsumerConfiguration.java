package com.garganttua.events.spec.interfaces.context;

import com.garganttua.events.context.GGEventsContextDataflowInProcessMode;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextHighAvailabilityMode;
import com.garganttua.events.context.GGEventsContextOriginPolicy;

public interface IGGEventsContextConsumerConfiguration {

	GGEventsContextHighAvailabilityMode getHighAvailabilityMode();

	GGEventsContextDataflowInProcessMode getProcessMode();

	GGEventsContextOriginPolicy getOpolicy();

	GGEventsContextDestinationPolicy getDpolicy();

}
