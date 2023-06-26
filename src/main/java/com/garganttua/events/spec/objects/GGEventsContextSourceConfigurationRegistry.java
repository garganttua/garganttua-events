package com.garganttua.events.spec.objects;

import java.util.HashMap;

import com.garganttua.events.spec.interfaces.IGGEventsContextSourceConfigurationRegistry;

public class GGEventsContextSourceConfigurationRegistry implements IGGEventsContextSourceConfigurationRegistry {

	private HashMap<String, String[]> configurations = new HashMap<String, String[]>();
	
	@Override
	public void registerContextSourceConfiguration(GGEventsContextSourceConfiguration configuration) {
		this.configurations.put(configuration.getSourceName(), configuration.getConfiguration());
	}
	
	@Override
	public String[] getContextSourceConfiguration(String name) {
		return this.configurations.get(name);
	}

}
