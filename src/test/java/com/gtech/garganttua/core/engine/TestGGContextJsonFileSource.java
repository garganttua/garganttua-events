/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.gtech.garganttua.core.context.GGContext;
import com.gtech.garganttua.core.context.sources.file.json.GGContextJsonFileSource;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;

class TestGGContextJsonFileSource {

	@Test
	void test() throws GGCoreException {
		GGContextJsonFileSource cjfs = new GGContextJsonFileSource();
		
//		String[] toto= {"C:\\Users\\JérémyCOLOMBET\\Desktop\\PERSO\\GARGAN~1\\GARGAN~3\\GA9E13~1\\GARGAN~2\\GARGAN~1\\src\\test\\RESOUR~1\\com\\gtech\\GARGAN~1\\FRAMEW~1\\context\\sources\\json\\file\\test.context"};
//		
//		cjfs.init("",toto);
//		
//		List<GGContext> context = cjfs.getContexts(null);
	}

}
