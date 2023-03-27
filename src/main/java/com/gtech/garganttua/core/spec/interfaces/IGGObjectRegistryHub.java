package com.gtech.garganttua.core.spec.interfaces;


import com.gtech.garganttua.core.spec.exceptions.GGCoreException;

public interface IGGObjectRegistryHub {

	void addObjectRegistry(String label, IGGObjectRegistry registry);

	Object getObject(String label) throws GGCoreException;
	
}
