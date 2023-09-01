package com.garganttua.events.context.json;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextTimeInterval;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTimeInterval;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextTimeInterval implements IGGEventsContextItemBinder<IGGEventsContextTimeInterval> {
	
	private long interval; 

	private TimeUnit timeUnit;
	
	@Override
	public IGGEventsContextTimeInterval bind() throws GGEventsException {
		return new GGEventsContextTimeInterval(this.interval, this.timeUnit);
	}

	@Override
	public void build(IGGEventsContextTimeInterval bound) throws GGEventsException {
		this.interval = bound.getInterval();
		this.timeUnit = bound.getTimeUnit();
	}

}
