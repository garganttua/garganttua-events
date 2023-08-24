package com.garganttua.events.spec.enums;

import lombok.Getter;

public enum GGEventsEventCriticity {
	FATAL("FATAL"), ERROR("ERROR"), WARN("WARN"), INFO("INFO"), DEBUG("DEBUG");
	
	@Getter
	private String label;

	GGEventsEventCriticity(String label) {
		this.label = label;
	}

}
