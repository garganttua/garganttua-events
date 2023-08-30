package com.garganttua.events.context.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextConsumerConfiguration;
import com.garganttua.events.context.GGEventsContextDataflowInProcessMode;
import com.garganttua.events.context.GGEventsContextDestinationPolicy;
import com.garganttua.events.context.GGEventsContextHighAvailabilityMode;
import com.garganttua.events.context.GGEventsContextOriginPolicy;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConsumerConfiguration;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class GGEventsJsonContextConsumerConfiguration implements IGGEventsContextItemBinder<IGGEventsContextConsumerConfiguration> {

	@JsonInclude
	private GGEventsContextDataflowInProcessMode processMode;
	@JsonInclude
	private GGEventsContextOriginPolicy originPolicy;
	@JsonInclude
	private GGEventsContextDestinationPolicy destinationPolicy;
	@JsonInclude
	private boolean ignoreAssetMessages;
	@JsonInclude
	private GGEventsContextHighAvailabilityMode highAvailabilityMode;
	@JsonInclude
	
	@Override
	public IGGEventsContextConsumerConfiguration bind() throws GGEventsException {
		return new GGEventsContextConsumerConfiguration(this.processMode, this.originPolicy, this.destinationPolicy, this.ignoreAssetMessages, this.highAvailabilityMode);
	}

	@Override
	public void build(IGGEventsContextConsumerConfiguration bound) throws GGEventsException {
		this.processMode = bound.getProcessMode();
		this.originPolicy = bound.getOpolicy();
		this.destinationPolicy = bound.getDpolicy();
		this.ignoreAssetMessages = bound.isIgnoreAssetMessages();
	}

}
