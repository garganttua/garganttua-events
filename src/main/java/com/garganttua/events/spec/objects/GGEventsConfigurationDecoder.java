/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.objects;

import java.net.URLDecoder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.garganttua.events.spec.exceptions.GGEventsCoreException;

public class GGEventsConfigurationDecoder {
	
	static public Map<String, List<String>> getConfigurationFromString(String query) {
		
	    if (query == null || query.isEmpty() ) {
	        return Collections.emptyMap();
	    }
	    return Arrays.stream(query.split("&")).map(t -> {
	    	 SimpleImmutableEntry<String, String> toto = null;
					try {
						toto = splitQueryParameter(t);
					} catch (GGEventsCoreException e) {
						e.printStackTrace();
					}
					return toto;
				})
	            .collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
	}

	private static SimpleImmutableEntry<String, String> splitQueryParameter(String it) throws GGEventsCoreException {
		try {
			if( it != null || !it.isEmpty() ) {
			    final int idx = it.indexOf("=");
			    final String key = idx > 0 ? it.substring(0, idx) : it;
			    final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
			    return new SimpleImmutableEntry<>(URLDecoder.decode(key, "UTF-8"),URLDecoder.decode(value, "UTF-8"));
			} return null;
		} catch(Exception e) {
			throw new GGEventsCoreException("Invalid configuration \""+it+"\"", e);
		}
	}

}
