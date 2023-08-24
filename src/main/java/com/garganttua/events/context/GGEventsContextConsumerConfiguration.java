/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextConsumerConfiguration {

	@JsonProperty(value ="inClusterProcessingMode",required = true)
	private GGEventsContextDataflowInProcessMode processMode;
	
	@JsonProperty(value="originPolicy", required = true)
	private GGEventsContextOriginPolicy opolicy;
	
	@JsonProperty(value="destinationPolicy", required = true)
	private GGEventsContextDestinationPolicy dpolicy;
//	
//	@JsonProperty(value="tenantPartioningPolicy", required = true)
//	private GGEventsContextTenantPartitioningPolicy tpolicy;
//	
	@JsonProperty(value ="ignoreAssetMessages", required = true)
	private boolean ignoreAssetMessages;
	
	@JsonProperty(value ="highAvailabilityMode", required = false)
	private GGEventsContextHighAvailabilityMode highAvailabilityMode = GGEventsContextHighAvailabilityMode.MASTER_SLAVE;
		
}
