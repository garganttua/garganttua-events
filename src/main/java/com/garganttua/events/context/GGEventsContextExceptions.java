package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextExceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GGEventsContextExceptions implements IGGEventsContextExceptions {
	
	private String to;
	
	private String cast;

	private String label;

}
