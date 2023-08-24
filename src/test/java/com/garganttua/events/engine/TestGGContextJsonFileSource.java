/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.garganttua.events.context.GGEventsContext;
import com.garganttua.events.context.sources.file.json.GGEventsContextJsonFileSource;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;

class TestGGEventsContextJsonFileSource {

	@Test
	void test() throws GGEventsCoreException {
		GGEventsContextJsonFileSource cjfs = new GGEventsContextJsonFileSource();
		
//		String[] toto= {"C:\\Users\\JérémyCOLOMBET\\Desktop\\PERSO\\GARGAN~1\\GARGAN~3\\GA9E13~1\\GARGAN~2\\GARGAN~1\\src\\test\\RESOUR~1\\com\\gtech\\GARGAN~1\\FRAMEW~1\\context\\sources\\json\\file\\test.context"};
//		
//		cjfs.init("",toto);
//		
//		List<GGEventsContext> context = cjfs.getContexts(null);
	}

}
