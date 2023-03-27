/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.connectors.kafka;

import java.time.Duration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGCoreKafkaClientConsumer implements Runnable {

	private Consumer<String, byte[]> consumer;
	private IGGMessageHandler messageHandler;
	private String topicRef;
	private String name;
	private String dataFlowUuid;
	private boolean garanteeOrder;

	public void stop() {
		this.consumer.wakeup();
	}

	public void run() {
		Duration timeout = Duration.ofMillis(10000);

		try {
			while (true) {
				
				ConsumerRecords<String, byte[]> records = this.consumer.poll(timeout);
				records.forEach(record -> {
					log.debug("Message received "+record.key()+ " : "+new String(record.value()));
					final byte[] message = record.value();
					final String dfUuid = record.key();

					if (message != null && dfUuid.equals(dataFlowUuid)) {

						if( this.garanteeOrder ) {
							try {
								messageHandler.handle(GGExchange.emptyExchange(this.name, this.topicRef, this.dataFlowUuid, message));
							} catch (GGCoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							//TODO : implémenter le handle dans un thread
							
							try {
								messageHandler.handle(GGExchange.emptyExchange(this.name, this.topicRef, this.dataFlowUuid, message));
							} catch (GGCoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
				this.consumer.commitSync();
			}
		} catch (WakeupException e) {
			// Nothing to do
		} finally {
			this.consumer.unsubscribe();
			this.consumer.close();
		}

	}

	public GGCoreKafkaClientConsumer(Consumer<String, byte[]> consumer, IGGMessageHandler messageHandler, String topicRef, String name, String dataFlowUuid, boolean garanteeOrder) {
		this.consumer = consumer;
		this.messageHandler = messageHandler;
		this.topicRef = topicRef;
		this.name = name;
		this.dataFlowUuid = dataFlowUuid;
		this.garanteeOrder = garanteeOrder;
	}
}
