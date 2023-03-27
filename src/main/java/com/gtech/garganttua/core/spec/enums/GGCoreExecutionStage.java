package com.gtech.garganttua.core.spec.enums;

import lombok.Getter;

public enum GGCoreExecutionStage {

	INIT("INIT"),
	STARTUP("STARTUP"), 
	RUN("RUN"),
	SHUTDOWN("SHUTDOWN");
	
	@Getter
	private String label;

	GGCoreExecutionStage(String label) {
		this.label = label;
	}

}
