/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import com.garganttua.events.context.GGEventsContextDataFlow;
import com.garganttua.events.spec.interfaces.IGGEventsDataflow;

import lombok.Getter;

@Getter
public class GGEventsDataflow implements IGGEventsDataflow {

	private GGEventsContextDataFlow dataflow;

	public GGEventsDataflow(GGEventsContextDataFlow dataflow) {
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
