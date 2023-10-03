package com.garganttua.events.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class GGEventsFilterProcessorTest {
	
	@Test
	public void testFindVariables() {
		
		List<String> vars = GGEventsFilterProcessor.findvariables("${var1} ${var2}");
		
		assertEquals(2, vars.size());
		
		assertTrue(vars.contains("${var1}"));
		assertTrue(vars.contains("${var2}"));
	}
	
	@Test
	public void testStringReplacement() {
		String str = "${exchange.value.json($.entityClass)}==com.garganttua.lcdt.api.tenants.fsdfdsf";
		
		String replaced = str.replace("${exchange.value.json($.entityClass)}", "ha");
		
		assertEquals(replaced, "ha==com.garganttua.lcdt.api.tenants.fsdfdsf");
	}

}
