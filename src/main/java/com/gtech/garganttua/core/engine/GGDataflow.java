/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import com.gtech.garganttua.core.context.GGContextDataFlow;
import com.gtech.garganttua.core.spec.interfaces.IGGDataflow;

import lombok.Getter;

@Getter
public class GGDataflow implements IGGDataflow {

	private GGContextDataFlow dataflow;

	public GGDataflow(GGContextDataFlow dataflow) {
		this.dataflow = dataflow;
	}

	@Override
	public String getUuid() {
		return this.dataflow.getUuid();
	}

	@Override
	public String getVersion() {
		return this.dataflow.getVersion();
	}

	@Override
	public boolean isEncapsulated() {
		return this.dataflow.isEncapsulated();
	}

}
