/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context.sources.file.json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.spec.annotations.GGEventsContextSource;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGEventsContextSource(type="GGEventsContextFileSource", version="1.0")
public class GGEventsContextJsonFileSource implements IGGEventsContextSource {

	private String files;
	private String assetId;

	public GGEventsContextJsonFileSource() {
	}
	
	public GGEventsContextJsonFileSource(String configuration) {
		// TODO Auto-generated constructor stub
	}

	//	@Override
	public void init(String assetId, String configuration) throws GGEventsException {
		this.assetId = assetId;
		this.files = configuration;
	}

//	@Override
	public List<GGEventsContext> readContext(Date now) throws GGEventsException {
		
		ObjectMapper mapper = new ObjectMapper();
		List<GGEventsContext> contexts = new ArrayList<GGEventsContext>();

		for( String file: this.files ) {
			
			if( file != null ) {
				log.info("Getting context from file "+file);
				
				GGEventsContext context = null;
			    try {
			    			    	
			    	byte[] fileBytes = Files.readAllBytes(Paths.get(file));
			    	String fileString = new String(fileBytes, StandardCharsets.UTF_8);
			    	context = mapper.readValue(fileString, GGEventsContext.class);
			    	
			    	String root = new String("file://"+Paths.get(file).toAbsolutePath().toString()); 
			    	String utf8 = new String(root.getBytes(), StandardCharsets.UTF_8);
			    	context.setSource(this.assetId, now, utf8);
			    	contexts.add(context);
				} catch (IOException e) {
					throw new GGEventsException("Cannot get context from file "+file, e);
				}
			}
		}
		
		return contexts;
	}

	@Override
	public GGEventsContext readContext() throws GGEventsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeContext() throws GGEventsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContext readContext(String configuration) throws GGEventsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeContext(String configuration) throws GGEventsException {
		// TODO Auto-generated method stub
		
	}
}
