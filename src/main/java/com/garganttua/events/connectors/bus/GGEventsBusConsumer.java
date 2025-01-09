package com.garganttua.events.connectors.bus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.garganttua.events.connectors.AbstractGGEventsConsumer;
import com.leansoft.bigqueue.IBigQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsBusConsumer extends AbstractGGEventsConsumer {

	private IBigQueue queue;
	private Integer gcInterval;
	private TimeUnit gcIntervalUnit;

	public GGEventsBusConsumer(IBigQueue queue, String topicRef, String name, Integer pollInterval, TimeUnit pollIntervalUnit, ExecutorService poolExecutor, Integer gcInterval, TimeUnit gcIntervalUnit) {
		super(topicRef, name, pollInterval, pollIntervalUnit, poolExecutor);
		this.queue = queue;
		this.gcInterval = gcInterval;
		this.gcIntervalUnit = gcIntervalUnit;
	}

	@Override
	protected Map<String, byte[]> doWaitForMessages(long timeout) {
		Map<String, byte[]> messages = new HashMap<String, byte[]>();
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (this.queue.isEmpty()) {
			return messages;
		}

		while (this.queue.isEmpty() != true) {
			try {
				byte[] bytes = this.queue.dequeue();
				GGEventsBusMessage message = GGEventsBusMessage.fromBytes(bytes);
				String dataflowUuid = message.getToDataflowUuid();
				
				messages.put(dataflowUuid, message.getValue());
				
			} catch (Exception e) {
				log.warn("[Connector:{}][Topic:{}][Timeout:{}] Error reading message from queue {}", this.name, this.topicRef, timeout, e.getMessage(), e);
			}
		}
		return messages;
	}
	
	@Override
	public void run() {
		super.run();
		this.poolExecutor.execute(new Thread() {
			@Override
			public void run() {
				while(true) {
					
					long time = TimeUnit.MILLISECONDS.convert(gcInterval, gcIntervalUnit);
					
					try {
						Thread.sleep(time);
					} catch (InterruptedException e) {
						log.warn("[Connector:{}][Topic:{}][Timeout:{}] Error reading queue GC {}", name, topicRef, time, e.getMessage(), e);
					}
					try {
						queue.gc();
					} catch (IOException e) {
						log.warn("[Connector:{}][Topic:{}][Timeout:{}] Error reading queue GC {}", name, topicRef, time, e.getMessage(), e);
					}
				}
			}
		});
	}

	@Override
	protected void ackMessages(Map<String, byte[]> receivedMessages) {
		//Nothing to do
	}

}
