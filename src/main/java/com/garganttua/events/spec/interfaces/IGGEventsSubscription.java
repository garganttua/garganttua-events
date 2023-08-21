/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import com.garganttua.events.context.GGEventsContextSubscription;

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
	
}
