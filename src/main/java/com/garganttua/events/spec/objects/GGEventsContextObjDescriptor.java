package com.garganttua.events.spec.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsContextObjDescriptor {
	
	protected String clazz;
	
	protected String type;
	
	protected String version;
	
	protected String infos;
	
	protected String configurationManual;

}
