package com.garganttua.events.context.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextLockObject;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextLockObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsJsonContextLockObject implements IGGEventsContextItemBinder<IGGEventsContextLockObject> {


	private String lock;

	private String lockObject;
	
	@Override
	public IGGEventsContextLockObject bind() throws GGEventsException {
		return new GGEventsContextLockObject(this.lock, this.lockObject);
	}

	@Override
	public void build(IGGEventsContextLockObject contextItem) throws GGEventsException {
		this.lock = contextItem.getLock();
		this.lockObject = contextItem.getLockObject();
	}

}
