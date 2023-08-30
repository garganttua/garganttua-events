package com.garganttua.events.context;

import java.util.Date;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GGEventsContextItemSource implements Comparable<GGEventsContextItemSource>, IGGEventsContextItemSource {
	
	private String assetId;
	
	private String clusterId;
	
	private String source;
	
	@Override
	public boolean equals(Object source) {
		return ((GGEventsContextItemSource) source).assetId.equals(this.assetId) &&
				((GGEventsContextItemSource) source).clusterId.equals(this.clusterId) &&
				((GGEventsContextItemSource) source).source.equals(this.source);
	}

	@Override
	public int compareTo(GGEventsContextItemSource o) {
		return this.assetId.compareTo(o.assetId) * this.clusterId.compareTo(o.clusterId) * this.source.compareTo(o.source);
	}
	
	
	@Override
	public int hashCode() {
		return this.assetId.hashCode() * this.clusterId.hashCode() * this.source.hashCode();
	}

}
