package com.gtech.garganttua.core.spec.objects;

import java.util.HashMap;

import com.gtech.garganttua.core.spec.interfaces.IGGContextSourceConfigurationRegistry;

public class GGContextSourceConfigurationRegistry implements IGGContextSourceConfigurationRegistry {

	private HashMap<String, String[]> configurations = new HashMap<String, String[]>();
	
	@Override
	public void registerContextSourceConfiguration(GGContextSourceConfiguration configuration) {
		this.configurations.put(configuration.getSourceName(), configuration.getConfiguration());
	}
	
	@Override
	public String[] getContextSourceConfiguration(String name) {
		return this.configurations.get(name);
	}

}
