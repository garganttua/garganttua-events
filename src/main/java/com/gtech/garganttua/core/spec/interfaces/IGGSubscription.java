/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;

import com.gtech.garganttua.core.context.GGContextSubscription;

public interface IGGSubscription {

	IGGProcessor getConsumerProcessor();

	IGGProcessor getProducerProcessor();

	IGGConsumer getConsumer();

	IGGProducer getProducer();
	
	IGGDataflow getDataflow();
	
	IGGConnector getConnector();
	
	GGContextSubscription getSubscription();
	String getId();
	
}
