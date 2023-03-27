/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGContextConsumerConfiguration {

	@JsonProperty(value ="inClusterProcessingMode",required = true)
	private GGContextDataflowInProcessMode processMode;
	
	@JsonProperty(value="originPolicy", required = true)
	private GGContextOriginPolicy opolicy;
	
	@JsonProperty(value="destinationPolicy", required = true)
	private GGContextDestinationPolicy dpolicy;
	
	@JsonProperty(value="tenantPartioningPolicy", required = true)
	private GGContextTenantPartitioningPolicy tpolicy;
	
	@JsonProperty(value ="ignoreAssetMessages", required = true)
	private boolean ignoreAssetMessages;
	
	@JsonProperty(value ="highAvailabilityMode", required = false)
	private GGContextHighAvailabilityMode highAvailabilityMode = GGContextHighAvailabilityMode.MASTER_SLAVE;
		
}
