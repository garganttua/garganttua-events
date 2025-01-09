package com.garganttua.events.engine;

import java.util.HashMap;
import java.util.Map;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistry;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;

public class GGEventsObjectRegistry implements IGGEventsObjectRegistryHub {
	
	private Map<String, IGGEventsObjectRegistry> objectRegistries = new HashMap<String, IGGEventsObjectRegistry>();
	
	@Override
	public void addObjectRegistry(String label, IGGEventsObjectRegistry registry) {
		this.objectRegistries.put(label, registry);
	}
	
	@Override
	public Object getObject(String ref) throws GGEventsException {
		String[] split = ref.split(":");
		
		return this.objectRegistries.get(split[0]).getObject(split[1]);
	}

}
