package com.garganttua.events.context.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextSource;
import com.garganttua.events.context.GGEventsContextSourcedItem;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSourcedItem;
import com.garganttua.events.spec.objects.GGEventsUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextSourceItem implements IGGEventsContextItemBinder<IGGEventsContextSource> {

	private String assetId;

	private String clusterId;

	private String source;
	
	@Override
	public IGGEventsContextSource bind() throws GGEventsException {
		return new GGEventsContextSource(this.assetId, this.clusterId, this.source);
	}

	@Override
	public void build(IGGEventsContextSource contextItem) throws GGEventsException {
		this.assetId = contextItem.getAssetId();
		this.clusterId = contextItem.getClusterId();
		this.source = contextItem.getSource();
	}

	public static void bindSources(GGEventsContextSourcedItem<?> item, List<GGEventsJsonContextSourceItem> sources) {
		sources.forEach(source -> {
			try {
				item.source(source.bind());
			} catch (GGEventsException e) {
				
			}
		});
	}

	public static void buildSources(GGEventsContextSourcedItem<?> item, List<GGEventsJsonContextSourceItem> sources) {
		item.getsources().forEach(source -> {
			GGEventsJsonContextSourceItem source_ = new GGEventsJsonContextSourceItem(source.getAssetId(), source.getClusterId(), source.getSource());
			if( !sources.contains(source_) ) {
				sources.add(source_);
			}
		});
	}

	public static void bindOtherVersions(GGEventsContextSourcedItem<?> item, List<?> otherVersions) {
		otherVersions.forEach(version -> {
			try {
				item.otherVersion((IGGEventsContextSourcedItem) ((IGGEventsContextItemBinder<?>) version).bind());
			} catch (GGEventsException e) {
			}
		});
	}

	public static <ContextItem extends IGGEventsContextSourcedItem, JsonItem extends IGGEventsContextItemBinder<ContextItem>> void buildOtherVersions(GGEventsContextSourcedItem<ContextItem> item, List<JsonItem> otherVersions, Class<JsonItem> clazz)  {
		item.getOtherVersions().forEach(version -> {
			
			JsonItem bound = null;
			try {
				bound = GGEventsUtils.getInstanceOf(clazz);
			} catch (GGEventsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bound.build(version);
			} catch (GGEventsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			otherVersions.add(bound);
		});
	}

//	public static <ContextItem extends IGGEventsContextSourcedItem, T extends IGGEventsContextItemBinder<ContextItem>> void buildOtherVersions(GGEventsContextSourcedItem<?> bound, List<T> otherVersions, Class<T> class1) {
//		// TODO Auto-generated method stub
//		
//	}


}
