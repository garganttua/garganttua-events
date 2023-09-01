package com.garganttua.events.context.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
		
		this.configureMapper(ignoreVersion, mapper);
		
		GGEventsJsonContext jsonContext = null;
		try {
			jsonContext = mapper.readValue(fileString, GGEventsJsonContext.class);
		} catch (JsonProcessingException e) {
			throw new GGEventsException(e);
		}
		return jsonContext;
	}

	private void configureMapper(boolean ignoreVersion, ObjectMapper mapper) {
		if( ignoreVersion ) {
			mapper.addMixIn(GGEventsJsonContextTopic.class, MixIn.class);
			mapper.addMixIn(GGEventsJsonContextDataflow.class, MixIn.class);
			mapper.addMixIn(GGEventsJsonContextSubscription.class, MixIn.class);
			mapper.addMixIn(GGEventsJsonContextConnector.class, MixIn.class);
			mapper.addMixIn(GGEventsJsonContextLock.class, MixIn.class);
			mapper.addMixIn(GGEventsJsonContextRoute.class, MixIn.class);
		}
	}

	@Override
	public String getStringFromContext(IGGEventsContext context) throws GGEventsException {
		return this.getStringFromContext(context, true);
	}

	@Override
	public String getStringFromContext(IGGEventsContext context, boolean ignoreVersion) throws GGEventsException {
		ObjectMapper mapper = new ObjectMapper();
		
		this.configureMapper(ignoreVersion, mapper);

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
	public IGGEventsContextItemBinder<IGGEventsContext> getBinderFromContext(IGGEventsContext context,
			boolean ignoreVersion) throws GGEventsException {
		GGEventsJsonContext contextBinder = new GGEventsJsonContext();
		contextBinder.build(context);
		return contextBinder;
	}

	private abstract class MixIn {
		@JsonIgnore
		List<GGEventsJsonContextSourceItem> sources;
		@JsonIgnore
		List<GGEventsJsonContextConnector> otherVersions;
	}
}
