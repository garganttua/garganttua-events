/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.exceptions;

public class GGCoreProcessingException extends GGCoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3031351058725723001L;

	public GGCoreProcessingException(Exception e) {
		super(e, e.getMessage());
	}

	public GGCoreProcessingException(String string) {
		super(string);
	}
	public GGCoreProcessingException(Exception e, String string) {
		super(e, string);
	}
}
