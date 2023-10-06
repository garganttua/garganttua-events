package com.garganttua.events.connectors.bus;

import java.io.IOException;

import com.garganttua.events.connectors.AbstractGGEventsProducer;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.leansoft.bigqueue.IBigQueue;

public class GGEventsBusProducer extends AbstractGGEventsProducer {

	private IBigQueue queue;
	private String dataflowUuid;

	public GGEventsBusProducer(IBigQueue bigQueue, String uuid) {
		queue = bigQueue;
		dataflowUuid = uuid;
	}

	@Override
	public void publishValue(byte[] value) throws GGEventsProcessingException {
		try {
			GGEventsBusMessage message = new GGEventsBusMessage(this.dataflowUuid, value);
			queue.enqueue(message.getBytes());
		} catch (IOException e) {
			throw new GGEventsProcessingException(e);
		}
	}

}
