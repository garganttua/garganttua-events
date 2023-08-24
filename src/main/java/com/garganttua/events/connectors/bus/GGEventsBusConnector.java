package com.garganttua.events.connectors.bus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.garganttua.events.context.GGEventsContextSubscription;
import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.exceptions.GGEventsConnectorException;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.leansoft.bigqueue.BigQueueImpl;

import lombok.Getter;
import lombok.Setter;

@GGEventsConnector(type="bus", version="1.0.0")
public class GGEventsBusConnector implements IGGEventsConnector {

	private static final String BUS_QUEUES_DIRECTORY = "homeDirectory";
	private static final String BUS_POLL_INTERVAL = "pollInterval";
	private static final String BUS_POLL_INTERVAL_UNIT = "pollIntervalUnit";
	private static final String BUS_GC_INTERVAL = "gcInterval";
	private static final String BUS_GC_INTERVAL_UNIT = "gcIntervalUnit";
	private String configuration;
	private ExecutorService poolExecutor;

	@Getter
	@Setter
	private String name;
	private Map<String, BigQueueImpl> queues = new HashMap<String, BigQueueImpl>();
	private String queuesDir;
	private Integer pollInterval;
	private TimeUnit pollIntervalUnit;
	private TimeUnit gcIntervalUnit;
	private Integer gcInterval;
	private Map<String, GGEventsBusConsumer> consumers = new HashMap<String, GGEventsBusConsumer>();
	private String infos;
	private String manual;

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsProcessingException, GGEventsException {
		String toTopic = exchange.getToTopic();
		
		BigQueueImpl queue = this.queues.get(toTopic);
		if( queue != null ) {
			try {
				GGEventsBusMessage message = new GGEventsBusMessage(exchange.getToDataflowUuid(), exchange.getValue());
				queue.enqueue(message.getBytes());
			} catch (IOException e) {
				throw new GGEventsProcessingException(e);
			}
		}
	}

	@Override
	public String getType() {
		return "IGGEventsConnector::bus";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) throws GGEventsException {
		this.configuration = configuration;
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(configuration);
		__configuration__.forEach((name, values) -> { 
			switch(name) {
			case BUS_QUEUES_DIRECTORY:
				this.queuesDir = values.get(0)+File.separator+assetId+File.separator+tenantId+File.separator+clusterId;
				break;
			case BUS_POLL_INTERVAL:
				this.pollInterval = Integer.valueOf(values.get(0));
				break;
			case BUS_POLL_INTERVAL_UNIT:
				this.pollIntervalUnit = TimeUnit.valueOf(values.get(0));
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

	@Override
	public void setPoolExecutor(ExecutorService poolExecutor) {
		this.poolExecutor = poolExecutor;
	}

	@Override
	public void start() throws GGEventsConnectorException {
		this.consumers.forEach((t,c) -> {
			c.start();
		});
		this.poolExecutor.execute(new Thread() {
			@Override
			public void run() {
				while(true) {
					
					long time = TimeUnit.MILLISECONDS.convert(gcInterval, gcIntervalUnit);
					
					try {
						Thread.sleep(time);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					queues.forEach((k,v) -> {
						try {
							v.gc();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
				}
			}
		});
	}

	@Override
	public void stop() throws GGEventsConnectorException {
//		this.poolExecutor.shutdown();
		
		this.consumers.forEach((t,c) -> {
			c.stop();
		});
		
		queues.forEach((k,v) -> {
			try {
				v.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Override
	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		
	}

	private String formatTopicRef(String topicRef) {
		return topicRef.replace("/", "-").substring(1);
	}

	@Override
	public void registerConsumer(GGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler, String tenantId,
			String clusterId, String assetId) {
		String topicRef = subscription.getTopic();
		String dataflowUuid = subscription.getDataFlow();
		try {
			BigQueueImpl queue = this.queues.get(topicRef);
			if ( queue == null ) {
				queue = new BigQueueImpl(this.queuesDir, this.formatTopicRef(topicRef));
				this.queues.put(topicRef, queue);
			} 

			GGEventsBusConsumer consumer = this.consumers.get(topicRef);
			
			if( consumer == null ) {
				consumer = new GGEventsBusConsumer(queue, topicRef, this.name, this.pollInterval, this.pollIntervalUnit, this.poolExecutor);
				this.consumers.put(topicRef, consumer);		
			}
			consumer.setGaranteeOrder(dataflowUuid, true);
			consumer.registerHandler(dataflowUuid, messageHandler);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void registerProducer(GGEventsContextSubscription subscription, String tenantId, String clusterId,
			String assetId) {
		String topicRef = subscription.getTopic();
		try {
			if (this.queues.get(topicRef) == null) {
				BigQueueImpl bigQueue = new BigQueueImpl(this.queuesDir, this.formatTopicRef(topicRef));
				this.queues.put(topicRef, bigQueue);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "bus", "1.0.0", this.infos, this.manual);
	}

}
