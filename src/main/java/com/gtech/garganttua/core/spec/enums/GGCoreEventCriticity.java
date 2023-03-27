package com.gtech.garganttua.core.spec.enums;

import lombok.Getter;

public enum GGCoreEventCriticity {
	FATAL("FATAL"), ERROR("ERROR"), WARN("WARN"), INFO("INFO"), DEBUG("DEBUG");
	
	@Getter
	private String label;

	GGCoreEventCriticity(String label) {
		this.label = label;
	}

}
