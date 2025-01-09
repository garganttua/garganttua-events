package com.garganttua.events.spec.objects;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;

public record GGEventsConnectorProducerRegistrationRequest(IGGEventsContextDataflow dataflow, IGGEventsContextSubscription subscription) {

}
