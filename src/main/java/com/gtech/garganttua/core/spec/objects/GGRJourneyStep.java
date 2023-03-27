/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.objects;

import java.util.Date;

import com.gtech.garganttua.core.spec.enums.GGRJourneyStepDirection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGRJourneyStep {

	protected Date date; 
	
	protected String assetId;
	
	protected String subscriptionId;
	
	protected GGRJourneyStepDirection stepDirection;
	
	protected String dataflowVersion;
	
	protected String uuid;
	
	protected String clusterId;
}
