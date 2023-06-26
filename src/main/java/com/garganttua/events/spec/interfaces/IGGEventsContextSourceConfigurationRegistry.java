/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

import com.garganttua.events.spec.objects.GGEventsContextSourceConfiguration;

public interface IGGEventsContextSourceConfigurationRegistry {

	String[] getContextSourceConfiguration(String name);

	void registerContextSourceConfiguration(GGEventsContextSourceConfiguration configurations);

}
