package com.gtech.garganttua.core.spec.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGContextObjDescriptor {
	
	protected String clazz;
	
	protected String type;
	
	protected String version;
	
	protected String infos;
	
	protected String configurationManual;

}
