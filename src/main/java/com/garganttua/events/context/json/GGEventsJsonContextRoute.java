package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextRoute;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextExceptions;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextLockObject;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextProcessor;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextRoute;
import com.garganttua.events.spec.objects.context.GGEventsContextItemBinderUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextRoute implements IGGEventsContextItemBinder<IGGEventsContextRoute> {

	@JsonInclude
	private String uuid;
	@JsonInclude
	private String from;
	@JsonInclude
	private String to;
	@JsonInclude
	private List<GGEventsJsonContextProcessor> processors;
	@JsonInclude
	private GGEventsJsonContextExceptions exceptions;
	@JsonInclude
	private GGEventsJsonContextLockObject synchronization;
	
	@JsonInclude
	protected List<GGEventsJsonContextSourceItem> sources = new ArrayList<GGEventsJsonContextSourceItem>();
	@JsonInclude
	protected List<GGEventsJsonContextRoute> otherVersions = new ArrayList<GGEventsJsonContextRoute>();

	@Override
	public IGGEventsContextRoute bind() throws GGEventsException {
		GGEventsContextRoute ggEventsContextRoute = new GGEventsContextRoute(uuid, from, to);
		if( this.processors != null )
			GGEventsContextItemBinderUtils.bindList(this.processors, ggEventsContextRoute.getProcessors());
		if( this.exceptions != null )
			ggEventsContextRoute.exceptions(this.exceptions.bind());
		if( this.synchronization != null )
			ggEventsContextRoute.synchronization(this.synchronization.bind());
		GGEventsJsonContextSourceItem.bindSources(ggEventsContextRoute, this.sources);
		return ggEventsContextRoute;
	}

	@Override
	public void build(IGGEventsContextRoute bound) throws GGEventsException {
		this.uuid = bound.getUuid();
		this.from = bound.getFrom();
		this.to = bound.getTo();
		if( bound.getProcessors() != null ) {
			this.processors = new ArrayList<GGEventsJsonContextProcessor>();
			GGEventsContextItemBinderUtils.buildList(bound.getProcessors(), this.processors, GGEventsJsonContextProcessor.class);
		}
		if( bound.getExceptions() != null ) {
			this.exceptions = new GGEventsJsonContextExceptions();
			this.exceptions.build(bound.getExceptions());
		}
		if( bound.getSynchronization() != null ) {
			this.synchronization = new GGEventsJsonContextLockObject();
			this.synchronization.build(bound.getSynchronization());
		}
		GGEventsJsonContextSourceItem.buildSources(bound, this.sources);
	}

}