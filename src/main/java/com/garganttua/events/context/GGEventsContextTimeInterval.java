/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.concurrent.TimeUnit;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextTimeInterval;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GGEventsContextTimeInterval implements IGGEventsContextTimeInterval {
	
	private long interval; 

	private TimeUnit timeUnit;
	
	@Override
	public boolean equals(Object obj) {
		GGEventsContextTimeInterval item = (GGEventsContextTimeInterval) obj;
		return this.interval == item.interval
			&& this.timeUnit.equals(item.timeUnit);
	}
	
	@Override
	public int hashCode() {
		return (int) (this.interval * this.timeUnit.hashCode());
	}
}
