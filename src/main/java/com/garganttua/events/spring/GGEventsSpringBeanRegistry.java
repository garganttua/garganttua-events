package com.garganttua.events.spring;

import org.springframework.context.ApplicationContext;

import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistry;

public class GGEventsSpringBeanRegistry implements IGGEventsObjectRegistry {
	
	public final static String LABEL = "bean";

	private ApplicationContext context;

	public GGEventsSpringBeanRegistry(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Object getObject(String ref) {
		return this.context.getBean(ref);
	}

	@Override
	public String getLabel() {
		return GGEventsSpringBeanRegistry.LABEL;
	}

}
