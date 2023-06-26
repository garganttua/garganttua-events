package com.garganttua.events.context;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextLockObject {
	
	@JsonProperty
	private String lock;
	
	@JsonProperty
	private String lockObject;

}
