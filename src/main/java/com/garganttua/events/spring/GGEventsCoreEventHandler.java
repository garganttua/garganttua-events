package com.garganttua.events.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.garganttua.events.spec.enums.GGEventsCoreEventCriticity;
import com.garganttua.events.spec.enums.GGEventsCoreExecutionStage;
import com.garganttua.events.spec.interfaces.IGGEventsCoreEventHandler;
import com.garganttua.events.spec.objects.GGEventsCoreEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsCoreEventHandler implements IGGEventsCoreEventHandler {

	private ApplicationContext context;

	public GGEventsCoreEventHandler(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public void handleEvent(GGEventsCoreEvent event) {
//		System.out.println(event.getMessage());

		if (event.getStage() == GGEventsCoreExecutionStage.STARTUP
				&& event.getCriticity() == GGEventsCoreEventCriticity.FATAL) {
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			log.error("Fatal error during startup", event.getException());
			SpringApplication.exit(this.context);
		}
	}

}
