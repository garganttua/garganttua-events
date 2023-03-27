package com.gtech.garganttua.core.context;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGContextExceptions {
	
	@JsonProperty(value ="to",required = true)
	private String to;
	
	@JsonProperty(value ="cast",required = true)
	private String cast;
	
	@JsonProperty(value ="label",required = true)
	private String label;

}
