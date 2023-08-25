/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextConsumerConfiguration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextConsumerConfiguration implements IGGEventsContextConsumerConfiguration {

	private GGEventsContextDataflowInProcessMode processMode = GGEventsContextDataflowInProcessMode.EVERYBODY;
	
	private GGEventsContextOriginPolicy opolicy = GGEventsContextOriginPolicy.FROM_ANY;
	
	private GGEventsContextDestinationPolicy dpolicy = GGEventsContextDestinationPolicy.TO_ANY;

	private boolean ignoreAssetMessages = true;
	
	private GGEventsContextHighAvailabilityMode highAvailabilityMode = GGEventsContextHighAvailabilityMode.MASTER_SLAVE;
	
}
