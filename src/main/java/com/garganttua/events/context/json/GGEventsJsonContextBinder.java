package com.garganttua.events.context.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;
import com.garganttua.events.spec.objects.context.AGGEventsContextBinder;

public class GGEventsJsonContextBinder extends AGGEventsContextBinder {

	@Override
	public IGGEventsContextItemBinder<IGGEventsContext> getBinderFromString(String fileString, boolean ignoreVersion) throws GGEventsException {
		ObjectMapper mapper = new ObjectMapper();
		GGEventsJsonContext jsonContext = null;
		try {
			jsonContext = mapper.readValue(fileString, GGEventsJsonContext.class);
		} catch (JsonProcessingException e) {
			throw new GGEventsException(e);
		}
		return jsonContext;
	}

	@Override
	public String getStringFromContext(IGGEventsContext context) throws GGEventsException {
		return this.getStringFromContext(context, true);
	}

	@Override
	public String getStringFromContext(IGGEventsContext context, boolean ignoreVersion) throws GGEventsException {
		ObjectMapper mapper = new ObjectMapper();
		
		IGGEventsContextItemBinder<IGGEventsContext> binder = this.getBinderFromContext(context, ignoreVersion);
		 
		String contextAsString = null;
	
		try {
			contextAsString = mapper.writeValueAsString(binder);
		} catch (JsonProcessingException e) {
			throw new GGEventsException(e);
		}
		
		return contextAsString;
	}

	@Override
	public IGGEventsContextItemBinder<IGGEventsContext> getBinderFromContext(IGGEventsContext context, boolean ignoreVersion) throws GGEventsException {
		GGEventsJsonContext contextBinder = new GGEventsJsonContext();
		contextBinder.build(context);
		return contextBinder;
	}
}
