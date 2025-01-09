package com.garganttua.events.connectors;

import com.garganttua.events.spec.exceptions.GGEventsProcessingException;

public abstract class AbstractGGEventsProducer {
	public abstract void publishValue(byte[] value) throws GGEventsProcessingException;
}
