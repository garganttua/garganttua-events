/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextProducerConfiguration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GGEventsContextProducerConfiguration implements IGGEventsContextProducerConfiguration {

	private GGEventsContextDestinationPolicy dpolicy = GGEventsContextDestinationPolicy.TO_ANY;
	
	private String destinationUuid = null;
	

}
