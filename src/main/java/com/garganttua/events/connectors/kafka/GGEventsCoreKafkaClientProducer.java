/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.connectors.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GGEventsCoreKafkaClientProducer {

	private KafkaProducer<String, byte[]> kafkaProducer;
	private String topicRef;
	private String dataflowUuid;

	public GGEventsCoreKafkaClientProducer(KafkaProducer<String, byte[]> kafkaProducer, String topicRef, String dataflowUuid) {
		this.kafkaProducer = kafkaProducer;
		this.topicRef = topicRef;
		this.dataflowUuid = dataflowUuid;
	}

	public void publishValue(byte[] value) {
		ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(this.topicRef, this.dataflowUuid, value);

		this.kafkaProducer.send(record);
		log.debug("Message sent "+this.dataflowUuid+ " : "+new String(value));
	}

	public void stop() {
		this.kafkaProducer.close();
	}

}
