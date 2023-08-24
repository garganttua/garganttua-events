/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.objects;

import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;

import lombok.Getter;
import lombok.Setter;

public abstract class GGEventsAbstractProcessor implements IGGEventsProcessor {

	@Setter
	protected IGGEventsEngine contextEngine;
	
	@Getter
	@Setter
	protected String type;

}
