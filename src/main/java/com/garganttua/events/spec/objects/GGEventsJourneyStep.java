/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.objects;

import java.util.Date;

import com.garganttua.events.spec.enums.GGEventsJourneyStepDirection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsJourneyStep {

	protected Date date; 
	
	protected String assetId;
	
	protected String subscriptionId;
	
	protected GGEventsJourneyStepDirection stepDirection;
	
	protected String dataflowVersion;
	
	protected String uuid;
	
	protected String clusterId;
}
