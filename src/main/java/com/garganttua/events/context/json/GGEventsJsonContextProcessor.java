package com.garganttua.events.context.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextProcessor;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextProcessor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsJsonContextProcessor implements IGGEventsContextItemBinder<IGGEventsContextProcessor> {
	

	private String type;

	private String version;

	private String configuration;

	@Override
	public IGGEventsContextProcessor bind() throws GGEventsException {
		return new GGEventsContextProcessor(this.type, this.version, this.configuration);
	}

	@Override
	public void build(IGGEventsContextProcessor contextItem) throws GGEventsException {
		this.type = contextItem.getType();
		this.version = contextItem.getVersion();
		this.configuration = contextItem.getConfiguration();
	}

}
