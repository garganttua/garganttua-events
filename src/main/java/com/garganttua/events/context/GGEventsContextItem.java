/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextMergeableItem;
import com.garganttua.events.spec.interfaces.context.IGGEventsSourcedContextItem;


public abstract class GGEventsContextItem<T extends IGGEventsSourcedContextItem> implements IGGEventsSourcedContextItem, IGGEventsContextMergeableItem<T> {
	
	protected List<GGEventsContextItemSource> sources = new ArrayList<GGEventsContextItemSource>();
	
	protected List<T> fromOtherSources = new ArrayList<T>();

	@Override
	public List<GGEventsContextItemSource> getsources() {
		return this.sources;
	}

	@Override
	public void source(GGEventsContextItemSource source) {
		this.sources.add(source);
	}

	@Override
	public T merge(T item) {
		if( this.isEqualTo(item) ) {
			item.getsources().forEach(s -> {
				if( !this.sources.contains(s) ) {
					this.sources.add(s);
				}
			});
		} else {
			this.fromOtherSources.add(item);
		}
		return null;
	}

	protected abstract boolean isEqualTo(T item);

}
