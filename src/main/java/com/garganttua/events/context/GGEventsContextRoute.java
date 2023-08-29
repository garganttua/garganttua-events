/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextExceptions;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextLockObject;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextProcessor;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextRoute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GGEventsContextRoute extends GGEventsContextItem<GGEventsContextRoute> implements IGGEventsContextRoute {

	public GGEventsContextRoute(String uuid, String from, String to) {
		this(uuid, from, new ArrayList<IGGEventsContextProcessor>(), to, null, null);
	}
	
	public GGEventsContextRoute(String uuid, String from, List<IGGEventsContextProcessor> processors, String to, IGGEventsContextExceptions exceptions, IGGEventsContextLockObject synchronization) {
		this.uuid = uuid;
		this.from = from;
		this.processors = processors;
		this.to = to;
		this.exceptions = exceptions;
		this.synchronization = synchronization;
	}

	private String uuid;

	private String from;

	private List<IGGEventsContextProcessor> processors;

	private String to;
	
	private IGGEventsContextExceptions exceptions;
	
	private IGGEventsContextLockObject synchronization;

	private IGGEventsContext context;
	
	@Override
	public IGGEventsContextRoute processor(String type, String version, String configuration) {
		this.processors.add(new GGEventsContextProcessor(type, version, configuration));
		return this;	
	}

	@Override
	public IGGEventsContextRoute exceptions(String to, String cast, String label) {
		this.exceptions = new GGEventsContextExceptions(to, cast, label);
		return this;
	}

	@Override
	public IGGEventsContextRoute synchronization(String lock, String lockObject) {
		this.synchronization = new GGEventsContextLockObject(lock, lockObject);
		return this;	
	}

	@Override
	public void context(IGGEventsContext context) {
		this.context = context;
	}

	@Override
	public IGGEventsContext context() {
		return this.context;
	}

	@Override
	protected boolean isEqualTo(GGEventsContextRoute item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public IGGEventsContextRoute processor(IGGEventsContextProcessor processor) {
		this.processors.add(processor);
		return this;
	}

	@Override
	public IGGEventsContextRoute exceptions(IGGEventsContextExceptions exceptions) {
		this.exceptions = exceptions;
		return this;
	}

	@Override
	public IGGEventsContextRoute synchronization(IGGEventsContextLockObject synchronization) {
		this.synchronization = synchronization;
		return this;
	}

}
