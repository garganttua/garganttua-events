/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.exceptions;

public class GGConnectorException extends GGCoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6312550964696632632L;

	public GGConnectorException(String string) {
		super(string);
	}

	public GGConnectorException(Exception e) {
		super(e);
	}

	public GGConnectorException(String string, Exception e) {
		super(string, e);
	}
}
