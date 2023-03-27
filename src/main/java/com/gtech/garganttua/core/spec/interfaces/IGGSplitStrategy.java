package com.gtech.garganttua.core.spec.interfaces;


import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;

public interface IGGSplitStrategy {

	List<Entry<String, Entry<String, byte[]>>> split(String tenantId, byte[] value, Set<String> keySet) throws GGCoreProcessingException;

}
