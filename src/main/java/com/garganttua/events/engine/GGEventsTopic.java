/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextTopic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GGEventsTopic {
	
	private String ref;
	private IGGEventsContextTopic contextTopic;

	public GGEventsTopic(IGGEventsContextTopic topic) {
		this.ref = topic.getRef();
		this.contextTopic = topic;
	}

}
