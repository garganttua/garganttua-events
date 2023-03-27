/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.interfaces;


import java.util.concurrent.ScheduledExecutorService;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;

public interface IGGRoute extends IGGMessageHandler {

	public void stop() throws GGCoreException;

	void start(ScheduledExecutorService scheduledExecutorService) throws GGCoreException;

}
