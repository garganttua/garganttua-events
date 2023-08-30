/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextTopic;

import lombok.Getter;

public class GGEventsContextTopic extends GGEventsContextItem<IGGEventsContextTopic> implements IGGEventsContextTopic {
	
	public GGEventsContextTopic(String ref) {
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
	protected boolean isEqualTo(IGGEventsContextTopic item) {
		return this.ref.equals(((GGEventsContextTopic) item).ref);
	}
	
}
