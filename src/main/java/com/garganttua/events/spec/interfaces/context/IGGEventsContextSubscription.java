package com.garganttua.events.spec.interfaces.context;

import java.util.concurrent.TimeUnit;

import com.garganttua.events.context.GGEventsContextDataflowInProcessMode;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextHighAvailabilityMode;
import com.garganttua.events.context.GGEventsContextOriginPolicy;
import com.garganttua.events.context.GGEventsContextPublicationMode;

public interface IGGEventsContextSubscription extends IGGEventsContextItemLinkedToContext {

	IGGEventsContextSubscription producerConfiguration(GGEventsContextDestinationPolicy destinationPolicy,
			String destinationUuid);

	IGGEventsContextSubscription consumerConfiguration(GGEventsContextDataflowInProcessMode inProcessMode,
			GGEventsContextOriginPolicy originPolicy, GGEventsContextDestinationPolicy destinationPolicy,
			boolean ignoreAssetMessages, GGEventsContextHighAvailabilityMode haMode);
	
	IGGEventsContextSubscription producerConfiguration(IGGEventsContextProducerConfiguration configuration);

	IGGEventsContextSubscription consumerConfiguration(IGGEventsContextConsumerConfiguration configuration);
	
	IGGEventsContextSubscription timeInterval(IGGEventsContextTimeInterval timeInterval);
	
	IGGEventsContextSubscription timeInterval(long time, TimeUnit unit);

	String getConnector();

	String getId();

	String getDataflow();

	String getTopic();

	GGEventsContextPublicationMode getPublicationMode();

	IGGEventsContextConsumerConfiguration getCconfiguration();

	IGGEventsContextProducerConfiguration getPconfiguration();

	IGGEventsContextTimeInterval getTimeInterval();

}
