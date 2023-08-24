package com.garganttua.events.spec.enums;

import lombok.Getter;

public enum GGEventsExecutionStage {

	INIT("INIT"),
	STARTUP("STARTUP"), 
	RUN("RUN"),
	SHUTDOWN("SHUTDOWN");
	
	@Getter
	private String label;

	GGEventsExecutionStage(String label) {
		this.label = label;
	}

}
