package com.garganttua.events.context.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextProducerConfiguration;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextProducerConfiguration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextProducerConfiguration implements IGGEventsContextItemBinder<IGGEventsContextProducerConfiguration> {

	private GGEventsContextDestinationPolicy destinationPolicy;
	
	private String destinationUuid;
	
	@Override
	public IGGEventsContextProducerConfiguration bind() throws GGEventsException {
		return new GGEventsContextProducerConfiguration(this.destinationPolicy, this.destinationUuid);
	}

	@Override
	public void build(IGGEventsContextProducerConfiguration bound) throws GGEventsException {
		this.destinationPolicy = bound.getDpolicy();
		this.destinationUuid = bound.getDestinationUuid();
	}

}
