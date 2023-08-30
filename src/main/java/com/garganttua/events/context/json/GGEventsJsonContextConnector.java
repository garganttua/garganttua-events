package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextConnector;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextConnector;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsSourcedContextItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextConnector implements IGGEventsContextItemBinder<IGGEventsContextConnector> {

	@JsonInclude
	private String name; 
	@JsonInclude
	private String type; 
	@JsonInclude
	private String version;
	@JsonInclude
	private String configuration;
	
	@JsonInclude
	protected List<GGEventsJsonContextSourceItem> sources = new ArrayList<GGEventsJsonContextSourceItem>();
	@JsonInclude
	protected List<GGEventsJsonContextConnector> otherVersions = new ArrayList<GGEventsJsonContextConnector>();

	@Override
	public IGGEventsContextConnector bind() {
		GGEventsContextConnector item = new GGEventsContextConnector(this.name, this.type, this.version, this.configuration);
		GGEventsJsonContextSourceItem.bindSources(item, this.sources);
		return item;
	}
	
	@Override
	public void build(IGGEventsContextConnector bound) {
		this.name = bound.getName();
		this.type = bound.getType();
		this.version = bound.getVersion();
		this.configuration = bound.getConfiguration();
		GGEventsJsonContextSourceItem.buildSources(bound, this.sources);
		
	}
}
