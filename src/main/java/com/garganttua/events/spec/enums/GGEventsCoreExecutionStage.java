package com.garganttua.events.spec.enums;

import lombok.Getter;

public enum GGEventsCoreExecutionStage {

	INIT("INIT"),
	STARTUP("STARTUP"), 
	RUN("RUN"),
	SHUTDOWN("SHUTDOWN");
	
	@Getter
	private String label;

	GGEventsCoreExecutionStage(String label) {
		this.label = label;
	}

}
