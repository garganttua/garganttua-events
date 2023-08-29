package com.garganttua.events.engine;

import com.garganttua.events.spec.interfaces.IGGEventsExceptionSubscription;
import com.garganttua.events.spec.interfaces.IGGEventsSubscription;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextExceptions;

import lombok.Getter;

@Getter
public class GGEventsExceptionSubscription extends GGEventsSubscription implements IGGEventsExceptionSubscription {

	private String cast;
	private String label;

	public GGEventsExceptionSubscription(IGGEventsSubscription subscription, IGGEventsContextExceptions exceptions) {
		super(subscription.getDataflow(), subscription.getSubscription(), subscription.getConnector(), subscription.getTopic(), subscription.getAssetId(), subscription.getClusterId());
		this.cast = exceptions.getCast();
		this.label = exceptions.getLabel();
	}

}
