/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;


import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;

public interface IGGProcessor extends IGGMessageHandler, IGGConfigurable, IGGDescribable {
	
	void setType(String type);

	void setContextEngine(IGGContextEngine framework);

}
