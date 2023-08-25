package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextLockObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GGEventsContextLockObject implements IGGEventsContextLockObject {
	
	private String lock;
	
	private String lockObject;

}
