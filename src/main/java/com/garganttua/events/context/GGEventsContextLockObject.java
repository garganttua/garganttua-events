package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextLockObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GGEventsContextLockObject implements IGGEventsContextLockObject {
	
	private String lock;
	
	private String lockObject;
	
	@Override
	public boolean equals(Object obj) {
		GGEventsContextLockObject item = (GGEventsContextLockObject) obj;
		return item.lock.equals(item.lock)
				&& item.lockObject.equals(item.lockObject);
	}
	
	@Override
	public int hashCode() {
		return this.lock.hashCode() * this.lockObject.hashCode();
	}

}
