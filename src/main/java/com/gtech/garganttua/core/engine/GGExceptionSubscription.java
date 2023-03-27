package com.gtech.garganttua.core.engine;

import com.gtech.garganttua.core.context.GGContextExceptions;
import com.gtech.garganttua.core.spec.interfaces.IGGExceptionSubscription;

import lombok.Getter;

@Getter
public class GGExceptionSubscription extends GGSubscription implements IGGExceptionSubscription {

	private String cast;
	private String label;

	public GGExceptionSubscription(GGSubscription subscription, GGContextExceptions exceptions) {
		super(subscription.getDataflow(), subscription.getSubscription(), subscription.getConnector(), subscription.getTopic(), subscription.getAssetId(), subscription.getClusterId());
		this.cast = exceptions.getCast();
		this.label = exceptions.getLabel();
	}

}
