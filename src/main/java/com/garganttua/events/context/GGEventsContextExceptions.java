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
	
	@Override
	public boolean equals(Object obj) {
		GGEventsContextExceptions item = (GGEventsContextExceptions) obj;
		return item.to.equals(item.to)
				&& item.cast.equals(item.cast)
				&& item.label.equals(item.label);
	}
	
	@Override
	public int hashCode() {
		return this.to.hashCode() * this.cast.hashCode() * this.label.hashCode();
	}

}
