/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context.json.sources.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
@GGEventsContextSource(type="json-file", version="1.0")
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextJsonFileSource implements IGGEventsContextSource {

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
			log.info("Getting context from file "+configuration);
			URI file = new File(configuration).toURI();
				    	
	    	byte[] fileBytes = null;
			try {
				fileBytes = Files.readAllBytes(Paths.get(file));
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
		log.info("Writting context to file "+configuration);
		
		try {
			String contextAsString = binder.getStringFromContext(context);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(configuration));
		    writer.write(contextAsString);		    
		    writer.close();
		} catch (IOException e) {
			throw new GGEventsException(e);
		}
		
	}

	@Override
	public String getType() {
		return "source::json-file";
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
