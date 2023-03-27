package com.gtech.garganttua.core.context;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGContextItemSource implements Comparable<GGContextItemSource>{
	
	private String assetId;
	
	private String clusterId;
	
	private String source;
	
	private Date date;
	
	@Override
	public boolean equals(Object o) {
		if( this.assetId.equals(((GGContextItemSource) o).getAssetId()) && this.clusterId.equals(((GGContextItemSource) o).getClusterId()) && this.source.equals(((GGContextItemSource) o).getSource())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(GGContextItemSource o) {
		if( this.assetId.equals(o.getAssetId()) && this.clusterId.equals(o.getClusterId()) && this.source.equals(o.getSource())) {
			return 0;
		} else {
			return 1;
		}
	}

}
