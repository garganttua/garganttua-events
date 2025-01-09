/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.exceptions.GGEventsException;

public interface IGGEventsRoute extends IGGEventsMessageHandler {

	public void stop() throws GGEventsException;

	void start(ScheduledExecutorService scheduledExecutorService) throws GGEventsException;

}
