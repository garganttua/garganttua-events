package com.gtech.garganttua.core.spec.interfaces;


import com.gtech.garganttua.core.spec.exceptions.GGCoreException;

public interface IGGObjectRegistry {

	Object getObject(String ref) throws GGCoreException;

}
