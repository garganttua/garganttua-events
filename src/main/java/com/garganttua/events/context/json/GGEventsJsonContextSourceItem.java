package com.garganttua.events.context.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemSource;
import com.garganttua.events.spec.interfaces.context.IGGEventsSourcedContextItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextSourceItem implements IGGEventsContextItemBinder<IGGEventsContextItemSource> {
	
	@JsonInclude
	private String assetId;
	@JsonInclude
	private String clusterId;
	@JsonInclude
	private String source;

	@Override
	public IGGEventsContextItemSource bind() throws GGEventsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void build(IGGEventsContextItemSource contextItem) throws GGEventsException {
		// TODO Auto-generated method stub
		
	}
	
	public static void bindSources(IGGEventsSourcedContextItem item, List<GGEventsJsonContextSourceItem> sources) {
		sources.forEach(source -> {
			try {
				item.source(source.bind());
			} catch (GGEventsException e) {
				
			}
		});
	}

	public static void buildSources(IGGEventsSourcedContextItem item, List<GGEventsJsonContextSourceItem> sources) {
		item.getsources().forEach(source -> {
			
			GGEventsJsonContextSourceItem source_ = new GGEventsJsonContextSourceItem(source.getAssetId(), source.getClusterId(), source.getSource());
			
			if( !sources.contains(source_) ) {
				sources.add(source_);
			}
		});
	}

}
