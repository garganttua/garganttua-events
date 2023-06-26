/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.objects;

import com.garganttua.events.spec.interfaces.IGGEventsContextEngine;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;

import lombok.Getter;
import lombok.Setter;

public abstract class GGEventsAbstractProcessor implements IGGEventsProcessor {

	@Setter
	protected IGGEventsContextEngine contextEngine;
	
	@Getter
	@Setter
	protected String type;

}
