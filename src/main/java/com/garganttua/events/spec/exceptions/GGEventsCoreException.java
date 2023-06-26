/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true, allowSetters = true)
public class GGEventsCoreException extends Exception {
	
	@Getter
	@Setter
	@JsonProperty
	protected String clazz;

	public GGEventsCoreException(String string) {
		super(string);
	}

	public GGEventsCoreException(Exception e) {
		super(e);
	}

	public GGEventsCoreException(String string, Exception e) {
		super(string, e);
	}
	
	public GGEventsCoreException(Exception e, String string) {
		super(string, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3678312981565791110L;

}
