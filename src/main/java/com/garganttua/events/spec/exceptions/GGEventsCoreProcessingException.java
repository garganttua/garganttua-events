/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.exceptions;

public class GGEventsCoreProcessingException extends GGEventsCoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3031351058725723001L;

	public GGEventsCoreProcessingException(Exception e) {
		super(e, e.getMessage());
	}

	public GGEventsCoreProcessingException(String string) {
		super(string);
	}
	public GGEventsCoreProcessingException(Exception e, String string) {
		super(e, string);
	}
}
