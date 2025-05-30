/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.connectors.kafka;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.garganttua.events.connectors.AbstractGGEventsConnector;
import com.garganttua.events.connectors.AbstractGGEventsConsumer;
import com.garganttua.events.connectors.AbstractGGEventsProducer;
import com.garganttua.events.context.GGEventsContextDataflowInProcessMode;
import com.garganttua.events.context.GGEventsContextHighAvailabilityMode;
import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;

@GGEventsConnector(type = "kafka", version = "1.0")
public class GGEventsKafkaConnector extends AbstractGGEventsConnector {

	private static final String KAFKA_BROKER_URL_PARAM_NAME = "url";
	private static final String KAFKA_MAX_POLL_RECORDS_CONFIG_PARAM_NAME = "maxPollRecords";
	private static final String KAFKA_ENABLE_AUTO_COMMIT_CONFIG_PARAM_NAME = "enableAutoCommit";
	private static final String KAFKA_AUTO_OFFSET_RESET_CONFIG_PARAM_NAME = "autoOffsetReset";
	private static final String KAFKA_ALLOW_AUTO_CREATE_TOPICS_CONFIG = "allowAutoCreateTopics";
	private static final String KAFKA_PARTITIONS_AUTO_SCALLING_CONFIG = "partitionsAutoScalling";

	protected String kafkaBrokerUrl;
	protected int maxPollRecordsConfig = 1;
	protected String enableAutoCommitConfig = Boolean.toString(false);
	protected String autoOffsetResetConfig = OFFSET_RESET_LATEST;
	private boolean allowAutoCreateTopics = false;
	private boolean partitionsAutoScalling = false;

	private static String OFFSET_RESET_LATEST = "latest";
	private static String OFFSET_RESET_EARLIER = "earliest";

	private String infos;
	private String manual;

	private String formatTopicRef(String topicRef) {
		return topicRef.replace("/", "-").substring(1);
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(this.configuration);
		__configuration__.forEach((name, values) -> {
			switch (name) {
			case KAFKA_BROKER_URL_PARAM_NAME:
				this.kafkaBrokerUrl = values.get(0);
				break;
			case KAFKA_MAX_POLL_RECORDS_CONFIG_PARAM_NAME:
				this.maxPollRecordsConfig = Integer.valueOf(values.get(0));
				break;
			case KAFKA_ENABLE_AUTO_COMMIT_CONFIG_PARAM_NAME:
				this.enableAutoCommitConfig = values.get(0);
				break;
			case KAFKA_AUTO_OFFSET_RESET_CONFIG_PARAM_NAME:
				this.autoOffsetResetConfig = values.get(0);
				break;
			case KAFKA_ALLOW_AUTO_CREATE_TOPICS_CONFIG:
				this.allowAutoCreateTopics = Boolean.valueOf(values.get(0));
				break;
			case KAFKA_PARTITIONS_AUTO_SCALLING_CONFIG:
				this.partitionsAutoScalling = Boolean.valueOf(values.get(0));
				break;
			}
		});
	}

	
	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "kafka", "1.0", this.infos, this.manual);
	}

	@Override
	protected AbstractGGEventsProducer createProducer(IGGEventsContextDataflow dataflow, IGGEventsContextSubscription subscription) {
		Properties props = new Properties();

		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaBrokerUrl);
		props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, this.allowAutoCreateTopics);
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "P_" + assetId + "_" + subscription.getDataflow() + "_" + this.formatTopicRef(subscription.getTopic()));

		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

		if (subscription.getCconfiguration()
				.getHighAvailabilityMode() == GGEventsContextHighAvailabilityMode.LOAD_BALANCED) {
			props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
					"com.gtech.garganttua.core.connectors.kafka.GGEventsKafkaConnectorRoundRobinPartitioner");
		}
		KafkaProducer<String, byte[]> kafkaProducer = new KafkaProducer<>(props);
		
		return new GGEventsKafkaClientProducer(kafkaProducer, this.formatTopicRef(subscription.getTopic()), subscription.getDataflow());
	}

	@Override
	protected AbstractGGEventsConsumer createConsumer(IGGEventsContextDataflow dataflow, IGGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler) {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaBrokerUrl);
		String groupId = null;

		if (subscription.getCconfiguration().getProcessMode() == GGEventsContextDataflowInProcessMode.ONLY_ONE_CLUSTER_NODE) {
			groupId = "C_" + tenantId + "_" + subscription.getDataflow() + "_" + this.formatTopicRef(subscription.getTopic()) + "_"
					+ clusterId;
		} else {
			groupId = "C_" + tenantId + "_" + subscription.getDataflow() + "_" + this.formatTopicRef(subscription.getTopic()) + "_"
					+ clusterId + "_" + assetId;
		}
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "C_" + assetId + "_" + subscription.getDataflow() + "_" + this.formatTopicRef(subscription.getTopic()));

		props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, this.allowAutoCreateTopics);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, this.maxPollRecordsConfig);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, this.enableAutoCommitConfig);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.autoOffsetResetConfig);
		props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());

		if (subscription.getCconfiguration().getHighAvailabilityMode() == GGEventsContextHighAvailabilityMode.LOAD_BALANCED && this.partitionsAutoScalling) {

			try {

				AdminClient client = AdminClient.create(props);
				Map<String, ConsumerGroupDescription> groups = client
						.describeConsumerGroups(Collections.singletonList(groupId)).all().get(10, TimeUnit.SECONDS);

				int groupSize = groups.get(groupId).members().size();
				int newGroupSize = groupSize+1;

				Map<String, TopicDescription> topic = client
						.describeTopics(Collections.singletonList(this.formatTopicRef(subscription.getTopic()))).all()
						.get(10, TimeUnit.SECONDS);

				int partitionsNb = topic.get(this.formatTopicRef(subscription.getTopic())).partitions().size();

				if( newGroupSize > partitionsNb ) {
					Map<String, NewPartitions> newPartitionSet = new HashMap<>();
				    newPartitionSet.put(this.formatTopicRef(subscription.getTopic()), NewPartitions.increaseTo(newGroupSize++));
				    client.createPartitions(newPartitionSet);
				}
				
				client.close();
			} catch (Exception e) {

			}
		}

		Consumer<String, byte[]> __consumer__ = new KafkaConsumer<>(props);
		__consumer__.subscribe(Collections.singletonList(this.formatTopicRef(subscription.getTopic())));

		return new GGEventsKafkaClientConsumer(Boolean.valueOf(this.enableAutoCommitConfig), __consumer__, subscription.getTopic(), this.name, this.pollInterval, this.pollIntervalUnit, this.poolExecutor);
	}

	@Override
	protected void doStop() {
	}

	@Override
	protected void doStart() {
	}

}
