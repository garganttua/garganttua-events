package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextLock;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextLock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsJsonContextLock implements IGGEventsContextItemBinder<IGGEventsContextLock> {

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
	protected List<GGEventsJsonContextLock> otherVersions = new ArrayList<GGEventsJsonContextLock>();

	@JsonInclude
	public IGGEventsContextLock bind() {
		GGEventsContextLock ggEventsContextLock = new GGEventsContextLock(this.name, this.type, this.version, this.configuration);
		GGEventsJsonContextSourceItem.bindSources(ggEventsContextLock, this.sources);
		return ggEventsContextLock;
	}

	@Override
	public void build(IGGEventsContextLock bound) {
		this.name = bound.getName();
		this.type = bound.getType();
		this.version = bound.getVersion();
		this.configuration = bound.getConfiguration();
		GGEventsJsonContextSourceItem.buildSources(bound, this.sources);
	}

}
