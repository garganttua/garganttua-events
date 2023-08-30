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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.context.json.GGEventsJsonContext;
import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;
import com.garganttua.events.spec.interfaces.context.IGGEventsContext;

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
	public void writeContext(IGGEventsContext context) throws GGEventsException {
		this.writeContext(context, this.file);
	}

	@Override
	public IGGEventsContext readContext(String configuration) throws GGEventsException {
		ObjectMapper mapper = new ObjectMapper();
		IGGEventsContext context = null;
		GGEventsJsonContext jsonContext = null;
		if( configuration != null ) {
			log.info("Getting context from file "+configuration);
			URI file = new File(configuration).toURI();
			
		    try {
		    			    	
		    	byte[] fileBytes = Files.readAllBytes(Paths.get(file));
		    	String fileString = new String(fileBytes, StandardCharsets.UTF_8);
		    	jsonContext = mapper.readValue(fileString, GGEventsJsonContext.class);
		    	context= jsonContext.bind();
			} catch (Exception e) {
				throw new GGEventsException("Cannot get context from file "+file, e);
			}
		}
		return context;
	}

	@Override
	public void writeContext(IGGEventsContext context, String configuration) throws GGEventsException {
		ObjectMapper mapper = new ObjectMapper();
		log.info("Writting context to file "+configuration);
		
		try {
			
			GGEventsJsonContext jsonContext = new GGEventsJsonContext();
			jsonContext.build(context);
			String contextAsString = mapper.writeValueAsString(jsonContext);
			BufferedWriter writer = new BufferedWriter(new FileWriter(configuration));
		    writer.write(contextAsString);		    
		    writer.close();
		} catch (IOException e) {
			throw new GGEventsException(e);
		}
		
	}

	@Override
	public String getType() {
		return "json-file";
	}

	@Override
	public String getConfiguration() {
		return this.file;
	}
}
