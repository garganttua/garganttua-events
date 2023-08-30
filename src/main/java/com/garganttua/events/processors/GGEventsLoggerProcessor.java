/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.processors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@GGEventsProcessor(type="log", version="1.0")
@Slf4j
public class GGEventsLoggerProcessor implements IGGEventsProcessor {

	@Getter
	private String configuration;
	private String level;
	private boolean withMeta;
	private String infos;
	private String manual;
	private String type = "IGGEventsProcessor::GGEventsLoggerProcessor";
	
	@Override
	public void handle(GGEventsExchange message) {

		if( this.level.equals("INFO") ) {
			if( this.withMeta ) {
				log.info(new String(message.getValue()));
				if( message.getException() != null ) {
					log.info("Exception from exchange:", message.getException());
				}
			} else {
				log.info(new String(message.getValue()));
				if( message.getException() != null ) {
					log.info("Exception from exchange:", message.getException());
				}
			}
		}
		if( this.level.equals("WARN") ) {
			if( this.withMeta ) {
				log.warn(new String(message.getValue()));
				if( message.getException() != null ) {
					log.warn("Exception from exchange:", message.getException());
				}
			} else {
				log.warn(new String(message.getValue()));
				if( message.getException() != null ) {
					log.warn("Exception from exchange:", message.getException());
				}
			}
		}
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) {
		String[] params = configuration.split("&");
	
		for( int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=");
			if( p[0].equals("level") ) {
				this.level = p[1];
			}
			if( p[0].equals("withMetaData") && p[1].equals("true") ) {
				this.withMeta = true;
			}
		}
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "log", "1.0", this.infos, this.manual);
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExecutorService(ExecutorService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScheduledExecutorService(ScheduledExecutorService service) {
		// TODO Auto-generated method stub
		
	}
}
