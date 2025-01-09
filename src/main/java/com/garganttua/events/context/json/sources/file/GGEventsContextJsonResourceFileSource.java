package com.garganttua.events.context.json.sources.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.garganttua.events.context.json.GGEventsJsonContextBinder;
import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextBinder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGEventsContextSource(type="json-resource-file", version="1.0")
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextJsonResourceFileSource implements IGGEventsContextSource {
	
	private String file;
	
	@Override
	public IGGEventsContext readContext() throws GGEventsException {
		return this.readContext(this.file);
	}

	@Override
	public IGGEventsContext readContext(String configuration) throws GGEventsException {
		return this.readContext(configuration, true);
	}	
	
	@Override
	public IGGEventsContext readContext(String configuration, boolean ignoreSources) throws GGEventsException {
		
		IGGEventsContext context = null;
		IGGEventsContextBinder binder = new GGEventsJsonContextBinder();

		if( configuration != null ) {
			log.info("Getting context from resource file "+configuration);
			InputStream resource = getClass().getClassLoader().getResourceAsStream(configuration);
				    	
	    	byte[] fileBytes = null;
			try {
				fileBytes = resource.readAllBytes();
			} catch (IOException e) {
				throw new GGEventsException(e);
			}
	    	String fileString = new String(fileBytes, StandardCharsets.UTF_8);
	    	
	    	context = binder.getContextFromString(fileString, ignoreSources);
		    	
		}
		return context;
	}

	@Override
	public void writeContext(IGGEventsContext context) throws GGEventsException {
		this.writeContext(context, this.file);
	}
	
	@Override
	public void writeContext(IGGEventsContext context, String configuration) throws GGEventsException {
		this.writeContext(context, configuration, true);
	}
	
	@Override
	public void writeContext(IGGEventsContext context, String configuration, boolean ignoreVersion) throws GGEventsException {
		IGGEventsContextBinder binder = new GGEventsJsonContextBinder();
		log.info("Writting context to resource file "+configuration);
		
		throw new GGEventsException("Cannot write file ["+configuration+"] in resources");
	}

	@Override
	public String getType() {
		return "source::json-resource-file";
	}

	@Override
	public String getConfiguration() {
		return this.file;
	}

	@Override
	public void setConfiguration(String configuration) {
		file = configuration;
	}
}
