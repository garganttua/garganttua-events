/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextConsumerConfiguration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextConsumerConfiguration implements IGGEventsContextConsumerConfiguration {

	private GGEventsContextDataflowInProcessMode processMode = GGEventsContextDataflowInProcessMode.EVERYBODY;
	
	private GGEventsContextOriginPolicy opolicy = GGEventsContextOriginPolicy.FROM_ANY;
	
	private GGEventsContextDestinationPolicy dpolicy = GGEventsContextDestinationPolicy.TO_ANY;

	private boolean ignoreAssetMessages = true;
	
	private GGEventsContextHighAvailabilityMode highAvailabilityMode = GGEventsContextHighAvailabilityMode.MASTER_SLAVE;
	
	@Override
	public boolean equals(Object obj) {
		GGEventsContextConsumerConfiguration item = (GGEventsContextConsumerConfiguration) obj;
		return this.processMode.equals(item.getProcessMode())
				&& this.opolicy.equals(item.getOpolicy())
				&& this.dpolicy.equals(item.getDpolicy())
				&& this.ignoreAssetMessages == item.isIgnoreAssetMessages()
				&& highAvailabilityMode.equals(item.getHighAvailabilityMode());
	}
	
	@Override
	public int hashCode() {
		return this.processMode.hashCode() * this.opolicy.hashCode() * this.dpolicy.hashCode() * (ignoreAssetMessages==true?1:2) * this.highAvailabilityMode.hashCode();
	}
	
}
