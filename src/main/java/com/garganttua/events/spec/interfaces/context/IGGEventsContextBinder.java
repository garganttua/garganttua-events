package com.garganttua.events.spec.interfaces.context;

import com.garganttua.events.spec.exceptions.GGEventsException;

public interface IGGEventsContextBinder {

	IGGEventsContext getContextFromString(String fileString) throws GGEventsException;
	IGGEventsContext getContextFromString(String fileString, boolean ignoreVersion) throws GGEventsException;

	IGGEventsContextItemBinder<IGGEventsContext> getBinderFromString(String fileString, boolean ignoreVersion) throws GGEventsException;
	IGGEventsContextItemBinder<IGGEventsContext> getBinderFromString(String fileString) throws GGEventsException;

	String getStringFromContext(IGGEventsContext context) throws GGEventsException;
	String getStringFromContext(IGGEventsContext context, boolean ignoreVersion) throws GGEventsException;
	
	IGGEventsContextItemBinder<IGGEventsContext> getBinderFromContext(IGGEventsContext context) throws GGEventsException;
	IGGEventsContextItemBinder<IGGEventsContext> getBinderFromContext(IGGEventsContext context, boolean ignoreVersion) throws GGEventsException;

}
