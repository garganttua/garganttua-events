package com.garganttua.events.connectors.bus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.garganttua.events.connectors.AbstractGGEventsConnector;
import com.garganttua.events.connectors.AbstractGGEventsConsumer;
import com.garganttua.events.connectors.AbstractGGEventsProducer;
import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.leansoft.bigqueue.BigQueueImpl;
import com.leansoft.bigqueue.IBigQueue;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGEventsConnector(type="bus", version="1.0")
public class GGEventsBusConnector extends AbstractGGEventsConnector {

	private static final String BUS_QUEUES_DIRECTORY = "homeDirectory";
	private static final String BUS_GC_INTERVAL = "gcInterval";
	private static final String BUS_GC_INTERVAL_UNIT = "gcIntervalUnit";

	@Getter
	@Setter
	private String name;
	private Map<String, IBigQueue> queues = new HashMap<String, IBigQueue>();
	private String queuesDir;
	private Integer gcInterval;
	private TimeUnit gcIntervalUnit;
	private String infos;
	private String manual;

	@Override
	public void applyConfiguration() throws GGEventsException {
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(this.configuration);
		__configuration__.forEach((name, values) -> { 
			switch(name) {
			case BUS_QUEUES_DIRECTORY:
				this.queuesDir = values.get(0)+File.separator+assetId+File.separator+tenantId+File.separator+clusterId;
				break;
			case BUS_GC_INTERVAL:
				this.gcInterval = Integer.valueOf(values.get(0));
				break;
			case BUS_GC_INTERVAL_UNIT:
				this.gcIntervalUnit = TimeUnit.valueOf(values.get(0));
				break;
			}
		});
	}

	private String formatTopicRef(String topicRef) {
		return topicRef.replace("/", "-").substring(1);
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "bus", "1.0.0", this.infos, this.manual);
	}

	@Override
	protected AbstractGGEventsProducer createProducer(IGGEventsContextDataflow dataflow, IGGEventsContextSubscription subscription) {
		String topicRef = subscription.getTopic();
		IBigQueue bigQueue = this.queues.get(topicRef);
		if (bigQueue == null) {
			try {
				bigQueue = new BigQueueImpl(this.queuesDir, this.formatTopicRef(topicRef));
				this.queues.put(topicRef, bigQueue);
			} catch (IOException e) {
				log.warn("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}][Topic:{}] Unable to create queue {}", this.name, topicRef, e.getMessage(), e);
				return null;
			}
		}
		return new GGEventsBusProducer(bigQueue, dataflow.getUuid());
		
	}

	@Override
	protected AbstractGGEventsConsumer createConsumer(IGGEventsContextDataflow dataflow, IGGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler) {
		String topicRef = subscription.getTopic();
		IBigQueue queue = this.queues.get(topicRef);
		if ( queue == null ) {
			try {
				queue = new BigQueueImpl(this.queuesDir, this.formatTopicRef(topicRef));
				this.queues.put(topicRef, queue);
			} catch (IOException e) {
				log.warn("[" + assetId + "][" + tenantId + "][" + clusterId + "][Connector:{}][Topic:{}] Unable to create queue {}", this.name, topicRef, e.getMessage(), e);
				return null;
			}
		} 
		return new GGEventsBusConsumer(queue, topicRef, this.name, this.pollInterval, this.pollIntervalUnit, this.poolExecutor, this.gcInterval, this.gcIntervalUnit);
	}

	@Override
	protected void doStop() {
	}

	@Override
	protected void doStart() {
	}

}
