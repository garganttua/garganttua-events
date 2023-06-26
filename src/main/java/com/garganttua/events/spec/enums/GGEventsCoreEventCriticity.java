package com.garganttua.events.spec.enums;

import lombok.Getter;

public enum GGEventsCoreEventCriticity {
	FATAL("FATAL"), ERROR("ERROR"), WARN("WARN"), INFO("INFO"), DEBUG("DEBUG");
	
	@Getter
	private String label;

	GGEventsCoreEventCriticity(String label) {
		this.label = label;
	}

}
