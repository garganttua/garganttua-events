package com.garganttua.events.spec.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class GGEventsContextSourceConfiguration {

	protected String sourceName;
	
	protected String[] configuration;

}
