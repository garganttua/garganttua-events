package com.gtech.garganttua.core.spec.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class GGContextSourceConfiguration {

	protected String sourceName;
	
	protected String[] configuration;

}
