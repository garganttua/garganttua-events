/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemSource;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextMergeableItem;
import com.garganttua.events.spec.interfaces.context.IGGEventsSourcedContextItem;


public abstract class GGEventsContextItem<T extends IGGEventsSourcedContextItem> implements IGGEventsSourcedContextItem, IGGEventsContextMergeableItem<T> {
	
	protected List<IGGEventsContextItemSource> sources = new ArrayList<IGGEventsContextItemSource>();
	
	protected List<T> otherVersions = new ArrayList<T>();

	@Override
	public List<IGGEventsContextItemSource> getsources() {
		return this.sources;
	}

	@Override
	public void source(IGGEventsContextItemSource source) {
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
			this.otherVersions.add(item);
		}
		return null;
	}

	protected abstract boolean isEqualTo(T item);

}
