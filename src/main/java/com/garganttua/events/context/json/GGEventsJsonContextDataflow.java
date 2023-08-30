package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsJsonContextDataflow implements IGGEventsContextItemBinder<IGGEventsContextDataflow> {
	
	@JsonInclude
	protected String uuid;
	
	@JsonInclude
	protected String name;
	
	@JsonInclude
	protected String type;
	
	@JsonInclude
	protected boolean garanteeOrder;
	
	@JsonInclude
	protected String version;
	
	@JsonInclude
	protected boolean encapsulated;
	
	@JsonInclude
	protected List<GGEventsJsonContextSourceItem> sources = new ArrayList<GGEventsJsonContextSourceItem>();
	@JsonInclude
	protected List<GGEventsJsonContextDataflow> otherVersions = new ArrayList<GGEventsJsonContextDataflow>();

	@Override
	public IGGEventsContextDataflow bind() {
		GGEventsContextDataflow ggEventsContextDataflow = new GGEventsContextDataflow(this.uuid, this.name, this.type, this.garanteeOrder, this.version, this.encapsulated);
		GGEventsJsonContextSourceItem.bindSources(ggEventsContextDataflow, this.sources);
		return ggEventsContextDataflow;
	}

	@Override
	public void build(IGGEventsContextDataflow bound) {
		this.uuid = bound.getUuid();
		this.name = bound.getName();
		this.type = bound.getType();
		this.garanteeOrder = bound.isGaranteeOrder();
		this.version = bound.getVersion();
		this.encapsulated = bound.isEncapsulated();
		GGEventsJsonContextSourceItem.buildSources(bound, this.sources);
	}

}