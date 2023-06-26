/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GGEventsContextTopic extends GGEventsSourcedContextItem {
	
	public GGEventsContextTopic(String ref, List<GGEventsContextItemSource> sources) {
		super(sources);
		this.ref = ref;
	}

	@JsonProperty(value ="ref",required = true)
	private String ref;
	
}
