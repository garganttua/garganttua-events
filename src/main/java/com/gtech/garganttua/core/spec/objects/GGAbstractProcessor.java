/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.objects;

import com.gtech.garganttua.core.spec.interfaces.IGGContextEngine;
import com.gtech.garganttua.core.spec.interfaces.IGGProcessor;

import lombok.Getter;
import lombok.Setter;

public abstract class GGAbstractProcessor implements IGGProcessor {

	@Setter
	protected IGGContextEngine contextEngine;
	
	@Getter
	@Setter
	protected String type;

}
