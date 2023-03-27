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
public class GGContextProducerConfiguration {
	
	@JsonProperty(value="destinationPolicy", required = true)
	private GGContextDestinationPolicy dpolicy;
	
	@JsonProperty(value="destinationUuid", required = true)
	private String destinationUuid;
	

}
