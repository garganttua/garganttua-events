package com.garganttua.events.spec.enums;

public enum GGEventsMediaType {
	APPLICATION_JSON("application/json"), TEXT_PLAIN("text/plain");
	
	private String string;

	GGEventsMediaType(String string) {
		this.string = string;
	}

	public String toString() {
		return this.string;
	}

}
