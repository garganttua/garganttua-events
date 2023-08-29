package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextExceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GGEventsContextExceptions implements IGGEventsContextExceptions {
	
	@Getter
	private String to;
	
	@Getter
	private String cast;

	@Getter
	private String label;

}
