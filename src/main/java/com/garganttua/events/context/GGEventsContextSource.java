package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GGEventsContextSource implements Comparable<GGEventsContextSource>, IGGEventsContextSource {
	
	private String assetId;
	
	private String clusterId;
	
	private String source;
	
	@Override
	public boolean equals(Object source) {
		return ((GGEventsContextSource) source).assetId.equals(this.assetId) &&
				((GGEventsContextSource) source).clusterId.equals(this.clusterId) &&
				((GGEventsContextSource) source).source.equals(this.source);
	}

	@Override
	public int compareTo(GGEventsContextSource o) {
		return this.assetId.compareTo(o.assetId) * this.clusterId.compareTo(o.clusterId) * this.source.compareTo(o.source);
	}
	
	
	@Override
	public int hashCode() {
		return this.assetId.hashCode() * this.clusterId.hashCode() * this.source.hashCode();
	}

}
