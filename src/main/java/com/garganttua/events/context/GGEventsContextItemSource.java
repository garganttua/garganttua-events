package com.garganttua.events.context;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsContextItemSource implements Comparable<GGEventsContextItemSource>{
	
	private String assetId;
	
	private String clusterId;
	
	private String source;
	
	private Date date;
	
	@Override
	public boolean equals(Object o) {
		if( this.assetId.equals(((GGEventsContextItemSource) o).getAssetId()) && this.clusterId.equals(((GGEventsContextItemSource) o).getClusterId()) && this.source.equals(((GGEventsContextItemSource) o).getSource())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(GGEventsContextItemSource o) {
		if( this.assetId.equals(o.getAssetId()) && this.clusterId.equals(o.getClusterId()) && this.source.equals(o.getSource())) {
			return 0;
		} else {
			return 1;
		}
	}

}
