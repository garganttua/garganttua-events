package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.context.GGEventsContextConnector;
import com.garganttua.events.context.GGEventsContextSourcedItem;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConnector;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextConnector implements IGGEventsContextItemBinder<IGGEventsContextConnector> {


	private String name; 

	private String type; 

	private String version;

	private String configuration;
	
	protected List<GGEventsJsonContextSourceItem> sources = new ArrayList<GGEventsJsonContextSourceItem>();

	protected List<GGEventsJsonContextConnector> otherVersions = new ArrayList<GGEventsJsonContextConnector>();

	@Override
	public IGGEventsContextConnector bind() {
		GGEventsContextConnector item = new GGEventsContextConnector(this.name, this.type, this.version, this.configuration);
		GGEventsJsonContextSourceItem.bindSources(item, this.sources);
		GGEventsJsonContextSourceItem.bindOtherVersions(item, this.otherVersions);
		return item;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void build(IGGEventsContextConnector bound) {
		this.name = bound.getName();
		this.type = bound.getType();
		this.version = bound.getVersion();
		this.configuration = bound.getConfiguration();
		GGEventsJsonContextSourceItem.buildSources((GGEventsContextSourcedItem<?>) bound, this.sources);
		GGEventsJsonContextSourceItem.buildOtherVersions((GGEventsContextSourcedItem<IGGEventsContextConnector>) bound, this.otherVersions, GGEventsJsonContextConnector.class);
	}
}
