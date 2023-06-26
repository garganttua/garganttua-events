/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;


import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.spec.exceptions.GGEventsCoreException;

public interface IGGEventsRoute extends IGGEventsMessageHandler {

	public void stop() throws GGEventsCoreException;

	void start(ScheduledExecutorService scheduledExecutorService) throws GGEventsCoreException;

}
