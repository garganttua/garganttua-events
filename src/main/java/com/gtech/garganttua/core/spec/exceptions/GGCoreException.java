/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true, allowSetters = true)
public class GGCoreException extends Exception {
	
	@Getter
	@Setter
	@JsonProperty
	protected String clazz;

	public GGCoreException(String string) {
		super(string);
	}

	public GGCoreException(Exception e) {
		super(e);
	}

	public GGCoreException(String string, Exception e) {
		super(string, e);
	}
	
	public GGCoreException(Exception e, String string) {
		super(string, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3678312981565791110L;

}
