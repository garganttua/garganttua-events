/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextTopic;

import lombok.Getter;

public class GGEventsContextTopic extends GGEventsContextItem<GGEventsContextTopic> implements IGGEventsContextTopic {
	
	public GGEventsContextTopic(String ref) {
		this(ref, new ArrayList<GGEventsContextItemSource>());
	}
	
	public GGEventsContextTopic(String ref, List<GGEventsContextItemSource> sources) {
		this.sources.addAll(sources);
		this.ref = ref;
	}

	@Getter
	private String ref;
	
	@Override
	public boolean equals(Object topic) {
		return this.ref.equals(((GGEventsContextTopic) topic).ref);
	}

	@Override
	public int hashCode() {
		return this.ref.hashCode();
	}

	@Override
	protected boolean isEqualTo(GGEventsContextTopic item) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
