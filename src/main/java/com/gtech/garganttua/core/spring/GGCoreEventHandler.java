package com.gtech.garganttua.core.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.gtech.garganttua.core.spec.enums.GGCoreEventCriticity;
import com.gtech.garganttua.core.spec.enums.GGCoreExecutionStage;
import com.gtech.garganttua.core.spec.interfaces.IGGCoreEventHandler;
import com.gtech.garganttua.core.spec.objects.GGCoreEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGCoreEventHandler implements IGGCoreEventHandler {

	private SpringApplication application;
	private ApplicationContext context;

	public GGCoreEventHandler(SpringApplication application, ApplicationContext context) {
		this.application = application;
		this.context = context;
	}

	@Override
	public void handleEvent(GGCoreEvent event) {
//		System.out.println(event.getMessage());

		if (event.getStage() == GGCoreExecutionStage.STARTUP
				&& event.getCriticity() == GGCoreEventCriticity.FATAL) {
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			log.error("Fatal error during startup", event.getException());
			this.application.exit(this.context, null);
		}
	}

}
