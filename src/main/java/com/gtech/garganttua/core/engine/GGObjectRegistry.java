package com.gtech.garganttua.core.engine;

import java.util.HashMap;
import java.util.Map;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistry;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;

public class GGObjectRegistry implements IGGObjectRegistryHub {
	
	private Map<String, IGGObjectRegistry> objectRegistries = new HashMap<String, IGGObjectRegistry>();
	
	@Override
	public void addObjectRegistry(String label, IGGObjectRegistry registry) {
		this.objectRegistries.put(label, registry);
	}
	
	@Override
	public Object getObject(String ref) throws GGCoreException {
		String[] split = ref.split(":");
		
		return this.objectRegistries.get(split[0]).getObject(split[1]);
	}

}
