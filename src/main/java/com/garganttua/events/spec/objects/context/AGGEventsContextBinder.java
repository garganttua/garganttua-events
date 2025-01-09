package com.garganttua.events.spec.objects.context;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextBinder;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;

public abstract class AGGEventsContextBinder implements IGGEventsContextBinder {

	@Override
	public IGGEventsContextItemBinder<IGGEventsContext> getBinderFromString(String fileString) throws GGEventsException {
		return this.getBinderFromString(fileString, true);
	}
	
	@Override
	public IGGEventsContext getContextFromString(String fileString, boolean ignoreVersion) throws GGEventsException {
		IGGEventsContext context = null;
		IGGEventsContextItemBinder<IGGEventsContext> contextBinder = this.getBinderFromString(fileString, ignoreVersion);
		context = contextBinder.bind();
		return context;
	}

	@Override
	public IGGEventsContext getContextFromString(String fileString) throws GGEventsException {
		return this.getContextFromString(fileString, true);
	}

	@Override
	public IGGEventsContextItemBinder<IGGEventsContext> getBinderFromContext(IGGEventsContext context) throws GGEventsException {
		return this.getBinderFromContext(context, true);
	}
}
