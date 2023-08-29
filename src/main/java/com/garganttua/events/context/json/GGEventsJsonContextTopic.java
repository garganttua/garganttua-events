package com.garganttua.events.context.json;

import com.fasterxml.jackson.annotation.JsonInclude;
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
	
	@JsonInclude
	private String ref;

	@Override
	public IGGEventsContextTopic bind() {
		return new GGEventsContextTopic(this.ref);
	}

	@Override
	public void build(IGGEventsContextTopic bound) {
		this.ref = bound.getRef();
	}

}
