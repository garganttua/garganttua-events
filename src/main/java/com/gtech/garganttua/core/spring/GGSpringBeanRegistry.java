package com.gtech.garganttua.core.spring;

import org.springframework.context.ApplicationContext;

import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistry;

public class GGSpringBeanRegistry implements IGGObjectRegistry {

	private ApplicationContext context;

	public GGSpringBeanRegistry(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Object getObject(String ref) {
		return this.context.getBean(ref);
	}

}
