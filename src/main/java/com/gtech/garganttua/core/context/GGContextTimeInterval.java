/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GGContextTimeInterval {
	
	@JsonProperty(value ="interval",required = true)
	private int interval; 
	
	@JsonProperty(value ="timeUnit",required = true)
	private TimeUnit timeUnit;
}
