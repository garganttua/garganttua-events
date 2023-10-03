package com.garganttua.events.spec.exceptions;

public class GGEventsHandlingException extends Exception {

	private static final long serialVersionUID = -7842510296431231301L;

	public GGEventsHandlingException(Exception e) {
		super(e);
	}

	public GGEventsHandlingException(String string) {
		super(string);
	}

}
