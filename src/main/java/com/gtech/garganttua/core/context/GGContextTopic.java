/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GGContextTopic extends GGSourcedContextItem {
	
	public GGContextTopic(String ref, List<GGContextItemSource> sources) {
		super(sources);
		this.ref = ref;
	}

	@JsonProperty(value ="ref",required = true)
	private String ref;
	
}
