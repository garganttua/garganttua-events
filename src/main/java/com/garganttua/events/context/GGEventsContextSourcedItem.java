/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import java.util.ArrayList;
import java.util.List;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextMergeableItem;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSourcedItem;


public abstract class GGEventsContextSourcedItem<T extends IGGEventsContextSourcedItem> implements IGGEventsContextSourcedItem, IGGEventsContextMergeableItem<T> {
	
	protected List<IGGEventsContextSource> sources = new ArrayList<IGGEventsContextSource>();
	
	protected List<T> otherVersions = new ArrayList<T>();

	@Override
	public List<IGGEventsContextSource> getsources() {
		return this.sources;
	}

	@Override
	public void source(IGGEventsContextSource source) {
		if( !this.sources.contains(source) )
			this.sources.add(source);
	}
	
	@Override
	public List<T> getOtherVersions() {
		return this.otherVersions;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void otherVersion(IGGEventsContextSourcedItem version) {
		if( this.otherVersions.contains(version) ) {
			T versionToUpdate = this.otherVersions.get(this.otherVersions.indexOf(version));
			version.getsources().forEach(s -> {
				versionToUpdate.source(s);
			});
		} else {
			this.otherVersions.add((T) version);
		}
	}

	@Override
	public void merge(T item) {
		if( this.isEqualTo(item) ) {
			item.getsources().forEach(s -> {
				this.source(s);
			});
		} else if (this.equals(item) ){
			this.otherVersion(item);
		} else {
			//nothing to do
		}
	}

	protected abstract boolean isEqualTo(T item);

}
