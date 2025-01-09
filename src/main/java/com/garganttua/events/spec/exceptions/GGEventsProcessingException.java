/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.exceptions;

public class GGEventsProcessingException extends GGEventsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3031351058725723001L;

	public GGEventsProcessingException(Exception e) {
		super(e, e.getMessage());
	}

	public GGEventsProcessingException(String string) {
		super(string);
	}
	public GGEventsProcessingException(Exception e, String string) {
		super(e, string);
	}
}
