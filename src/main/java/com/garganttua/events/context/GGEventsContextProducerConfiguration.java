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
public class GGEventsContextProducerConfiguration implements IGGEventsContextProducerConfiguration {

	@Getter
	private GGEventsContextDestinationPolicy dpolicy = GGEventsContextDestinationPolicy.TO_ANY;
	
	@Getter
	private String destinationUuid = null;
	
}
