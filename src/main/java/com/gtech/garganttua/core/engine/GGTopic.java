/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import com.gtech.garganttua.core.context.GGContextTopic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GGTopic {
	
	private String ref;
	private GGContextTopic contextTopic;

	public GGTopic(GGContextTopic topic) {
		this.ref = topic.getRef();
		this.contextTopic = topic;
	}

}
