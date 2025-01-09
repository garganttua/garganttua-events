/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.connectors.kafka;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.garganttua.events.connectors.AbstractGGEventsConsumer;

public class GGEventsKafkaClientConsumer extends AbstractGGEventsConsumer {
	
	private Consumer<String, byte[]> consumer;
	private boolean autoCommit;

	public GGEventsKafkaClientConsumer(boolean autoCommit, Consumer<String, byte[]> consumer, String topicRef, String name, Integer pollInterval, TimeUnit pollIntervalUnit, ExecutorService poolExecutor) {
		super(topicRef, name, pollInterval, pollIntervalUnit, poolExecutor);
		this.autoCommit = autoCommit;
		this.consumer = consumer;
	}

	@Override
	protected Map<String, byte[]> doWaitForMessages(long timeout) {
		Map<String, byte[]> messages = new HashMap<String, byte[]>();
		Duration duration = Duration.ofMillis(timeout);
		ConsumerRecords<String, byte[]> records = this.consumer.poll(duration);
		records.forEach(record -> {
			final byte[] message = record.value();
			final String dfUuid = record.key();
			messages.put(dfUuid, message);
		});
		return messages;
	}

	@Override
	protected void ackMessages(Map<String, byte[]> receivedMessages) {
		if( !this.autoCommit ) {
			this.consumer.commitSync();
		}
	}
}
