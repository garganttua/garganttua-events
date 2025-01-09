package com.garganttua.events.spec.objects;

import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;

public record GGEventsConnectorConsumerRegistrationRequest(IGGEventsContextDataflow dataflow, IGGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler) {

}
