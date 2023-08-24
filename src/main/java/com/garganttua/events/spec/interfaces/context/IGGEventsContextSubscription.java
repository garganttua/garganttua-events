package com.garganttua.events.spec.interfaces.context;

import com.garganttua.events.context.GGEventsContextConsumerConfiguration;
import com.garganttua.events.context.GGEventsContextDataflowInProcessMode;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextHighAvailabilityMode;
import com.garganttua.events.context.GGEventsContextOriginPolicy;
import com.garganttua.events.context.GGEventsContextProducerConfiguration;

public class IGGEventsContextSubscription {

	public IGGEventsContextSubscription producerConfiguration(GGEventsContextDestinationPolicy destinationPolicy, String destinationUuid) {
		return null;
		// TODO Auto-generated method stub
		
	}

	public IGGEventsContextSubscription consumerConfiguration(GGEventsContextDataflowInProcessMode inProcessMode,
			GGEventsContextOriginPolicy originPolicy, GGEventsContextDestinationPolicy destinationPolicy, boolean ignoreAssetMessages,
			GGEventsContextHighAvailabilityMode haMode) {
				return null;
		// TODO Auto-generated method stub
		
	}

	public IGGEventsContext context() {
		// TODO Auto-generated method stub
		return null;
	}

}
