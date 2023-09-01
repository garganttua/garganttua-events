package com.garganttua.events.context.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextSourcedItem;
import com.garganttua.events.context.GGEventsContextTopic;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextTopic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsJsonContextTopic implements IGGEventsContextItemBinder<IGGEventsContextTopic> {
	
	private String ref;

	protected List<GGEventsJsonContextSourceItem> sources = new ArrayList<GGEventsJsonContextSourceItem>();
	
	@Override
	public IGGEventsContextTopic bind() {
		GGEventsContextTopic ggEventsContextTopic = new GGEventsContextTopic(this.ref);
		GGEventsJsonContextSourceItem.bindSources(ggEventsContextTopic, this.sources);
		return ggEventsContextTopic;
	}

	@Override
	public void build(IGGEventsContextTopic bound) {
		this.ref = bound.getRef();
		GGEventsJsonContextSourceItem.buildSources((GGEventsContextSourcedItem<?>) bound, this.sources);
	}

}
