/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.interfaces;

public interface IGGEventsEngine {

	IGGEventsEngine start();

	IGGEventsEngine stop();

	IGGEventsEngine reload();

	IGGEventsAssetInfos getAssetInfos();

}
