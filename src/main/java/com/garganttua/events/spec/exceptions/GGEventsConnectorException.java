/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.exceptions;

public class GGEventsConnectorException extends GGEventsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6312550964696632632L;

	public GGEventsConnectorException(String string) {
		super(string);
	}

	public GGEventsConnectorException(Exception e) {
		super(e);
	}

	public GGEventsConnectorException(String string, Exception e) {
		super(string, e);
	}
}
