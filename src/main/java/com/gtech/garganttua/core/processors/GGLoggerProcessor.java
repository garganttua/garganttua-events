/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.processors;

import com.gtech.garganttua.core.spec.annotations.GGProcessor;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@GGProcessor(type="log", version="1.0.0")
@Slf4j
public class GGLoggerProcessor extends GGAbstractProcessor {

	@Getter
	private String configuration;
	private String level;
	private boolean withMeta;
	private String infos;
	private String manual;
	
	@Override
	public void handle(GGExchange message) {

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
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) {
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
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "log", "1.0.0", this.infos, this.manual);
	}
}
