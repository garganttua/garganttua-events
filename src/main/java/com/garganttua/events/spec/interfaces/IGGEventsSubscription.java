/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.engine.GGEventsTopic;

public interface IGGEventsSubscription {

	IGGEventsProcessor getInFilterProcessor();

	IGGEventsProcessor getOutFilterProcessor();

	IGGEventsConsumer getConsumer();

	IGGEventsProducer getProducer();
	
	IGGEventsDataflow getDataflow();
	
	IGGEventsConnector getConnector();
	
	GGEventsContextSubscription getSubscription();
	
	String getId();

	IGGEventsProcessor getProtocolInProcessor();
	
	IGGEventsProcessor getProtocolOutProcessor();

	GGEventsTopic getTopic();

	String getClusterId();

	String getAssetId();
	
}
