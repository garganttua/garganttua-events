package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.context.GGEventsContextDataflow;
import com.garganttua.events.context.GGEventsContextSourcedItem;
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
	

	protected String uuid;
	
	protected String name;
	
	protected String type;
	
	protected boolean garanteeOrder;
	
	protected String version;

	protected boolean encapsulated;

	protected List<GGEventsJsonContextSourceItem> sources = new ArrayList<GGEventsJsonContextSourceItem>();

	protected List<GGEventsJsonContextDataflow> otherVersions = new ArrayList<GGEventsJsonContextDataflow>();

	@Override
	public IGGEventsContextDataflow bind() {
		GGEventsContextDataflow ggEventsContextDataflow = new GGEventsContextDataflow(this.uuid, this.name, this.type, this.garanteeOrder, this.version, this.encapsulated);
		GGEventsJsonContextSourceItem.bindSources(ggEventsContextDataflow, this.sources);
		GGEventsJsonContextSourceItem.bindOtherVersions(ggEventsContextDataflow, this.otherVersions);
		return ggEventsContextDataflow;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void build(IGGEventsContextDataflow bound) {
		this.uuid = bound.getUuid();
		this.name = bound.getName();
		this.type = bound.getType();
		this.garanteeOrder = bound.isGaranteeOrder();
		this.version = bound.getVersion();
		this.encapsulated = bound.isEncapsulated();
		GGEventsJsonContextSourceItem.buildSources((GGEventsContextSourcedItem<?>) bound, this.sources);
		GGEventsJsonContextSourceItem.buildOtherVersions((GGEventsContextSourcedItem<IGGEventsContextDataflow>) bound, this.otherVersions, GGEventsJsonContextDataflow.class);
	}

}
