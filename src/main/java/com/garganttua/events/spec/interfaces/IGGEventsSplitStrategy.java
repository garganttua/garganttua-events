package com.garganttua.events.spec.interfaces;


import java.util.List;
import java.util.Map.Entry;

import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;

import java.util.Set;

public interface IGGEventsSplitStrategy {

	List<Entry<String, Entry<String, byte[]>>> split(String tenantId, byte[] value, Set<String> keySet) throws GGEventsCoreProcessingException;

}
