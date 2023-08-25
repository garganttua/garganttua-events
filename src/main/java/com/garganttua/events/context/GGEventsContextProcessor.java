/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.context;

import com.garganttua.events.spec.interfaces.context.IGGEventsContextProcessor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextProcessor implements IGGEventsContextProcessor {

	private String type;
	
	private String version;
	
	private String configuration;
	
	@Override
	public boolean equals(Object obj) {
		return this.type.equals(((GGEventsContextProcessor) obj).type) &&
				this.version.equals(((GGEventsContextProcessor) obj).version) &&
				this.configuration.equals(((GGEventsContextProcessor) obj).configuration);
	}
	
	@Override
	public int hashCode() {
		return this.type.hashCode() * this.version.hashCode() * this.configuration.hashCode();
	}
	
}
