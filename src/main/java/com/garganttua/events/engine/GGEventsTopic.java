/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import com.garganttua.events.context.GGEventsContextTopic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GGEventsTopic {
	
	private String ref;
	private GGEventsContextTopic contextTopic;

	public GGEventsTopic(GGEventsContextTopic topic) {
		this.ref = topic.getRef();
		this.contextTopic = topic;
	}

}
