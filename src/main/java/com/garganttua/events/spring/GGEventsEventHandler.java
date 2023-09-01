package com.garganttua.events.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.garganttua.events.spec.enums.GGEventsEventCriticity;
import com.garganttua.events.spec.enums.GGEventsExecutionStage;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.objects.GGEventsEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsEventHandler implements IGGEventsEventHandler {

	private ApplicationContext context;

	public GGEventsEventHandler(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public void handleEvent(GGEventsEvent event) {
//		System.out.println(event.getMessage());

		if (event.getStage() == GGEventsExecutionStage.STARTUP
				&& event.getCriticity() == GGEventsEventCriticity.FATAL) {
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
