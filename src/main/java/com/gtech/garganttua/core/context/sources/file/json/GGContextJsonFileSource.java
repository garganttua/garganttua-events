/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.context.sources.file.json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtech.garganttua.core.context.GGContext;
import com.gtech.garganttua.core.spec.annotations.GGContextSource;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGContextSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGContextSource(name="GGContextFileSource")
public class GGContextJsonFileSource implements IGGContextSource {
	
	private String[] files;
	private String assetId;

	@Override
	public void init(String assetId, String[] configuration) throws GGCoreException {
		this.assetId = assetId;
		this.files = configuration;
	}

	@Override
	public List<GGContext> getContexts(Date now) throws GGCoreException {
		
		ObjectMapper mapper = new ObjectMapper();
		List<GGContext> contexts = new ArrayList<GGContext>();

		for( String file: this.files ) {
			
			if( file != null ) {
				log.info("Getting context from file "+file);
				
				GGContext context = null;
			    try {
			    			    	
			    	byte[] fileBytes = Files.readAllBytes(Paths.get(file));
			    	String fileString = new String(fileBytes, StandardCharsets.UTF_8);
			    	context = mapper.readValue(fileString, GGContext.class);
			    	
			    	String root = new String("file://"+Paths.get(file).toAbsolutePath().toString()); 
			    	String utf8 = new String(root.getBytes(), StandardCharsets.UTF_8);
			    	context.setSource(this.assetId, now, utf8);
			    	contexts.add(context);
				} catch (IOException e) {
					throw new GGCoreException("Cannot get context from file "+file, e);
				}
			}
		}
		
		return contexts;
	}
}
