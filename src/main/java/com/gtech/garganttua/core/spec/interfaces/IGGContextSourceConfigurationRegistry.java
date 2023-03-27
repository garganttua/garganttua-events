/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;

import com.gtech.garganttua.core.spec.objects.GGContextSourceConfiguration;

public interface IGGContextSourceConfigurationRegistry {

	String[] getContextSourceConfiguration(String name);

	void registerContextSourceConfiguration(GGContextSourceConfiguration configurations);

}
