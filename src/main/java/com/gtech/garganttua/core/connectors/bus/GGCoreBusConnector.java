package com.gtech.garganttua.core.connectors.bus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.gtech.garganttua.core.context.GGContextSubscription;
import com.gtech.garganttua.core.spec.annotations.GGConnector;
import com.gtech.garganttua.core.spec.exceptions.GGConnectorException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGConfigurationDecoder;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;
import com.leansoft.bigqueue.BigQueueImpl;

import lombok.Getter;

@GGConnector(type="bus", version="1.0.0")
public class GGCoreBusConnector implements IGGConnector {

	private static final String BUS_QUEUES_DIRECTORY = "homeDirectory";
	private static final String BUS_POLL_INTERVAL = "pollInterval";
	private static final String BUS_POLL_INTERVAL_UNIT = "pollIntervalUnit";
	private static final String BUS_GC_INTERVAL = "gcInterval";
	private static final String BUS_GC_INTERVAL_UNIT = "gcIntervalUnit";
	private String configuration;
	private ExecutorService poolExecutor;

	@Getter
	private String name;
	private Map<String, BigQueueImpl> queues = new HashMap<String, BigQueueImpl>();
	private String queuesDir;
	private Integer pollInterval;
	private TimeUnit pollIntervalUnit;
	private TimeUnit gcIntervalUnit;
	private Integer gcInterval;
	private Map<String, GGCoreBusConsumer> consumers = new HashMap<String, GGCoreBusConsumer>();
	private int counter = 0;
	private String infos;
	private String manual;

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		String toTopic = exchange.getToTopic();
		
		BigQueueImpl queue = this.queues.get(toTopic);
		if( queue != null ) {
			try {
				GGBusMessage message = new GGBusMessage(exchange.getToDataflowUuid(), exchange.getValue());
				queue.enqueue(message.getBytes());
			} catch (IOException e) {
				throw new GGCoreProcessingException(e);
			}
		}
	}

	@Override
	public String getType() {
		return "IGGConnector::bus";
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) throws GGCoreException {
		this.configuration = configuration;
		Map<String, List<String>> __configuration__ = GGConfigurationDecoder.getConfigurationFromString(configuration);
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
	public void start() throws GGConnectorException {
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
	public void stop() throws GGConnectorException {
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
	public void applyConfiguration() throws GGCoreException {
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	private String formatTopicRef(String topicRef) {
		return topicRef.replace("/", "-").substring(1);
	}

	@Override
	public void registerConsumer(GGContextSubscription subscription, IGGMessageHandler messageHandler, String tenantId,
			String clusterId, String assetId) {
		String topicRef = subscription.getTopic();
		String dataflowUuid = subscription.getDataFlow();
		try {
			BigQueueImpl queue = this.queues.get(topicRef);
			if ( queue == null ) {
				queue = new BigQueueImpl(this.queuesDir, this.formatTopicRef(topicRef));
				this.queues.put(topicRef, queue);
			} 

			GGCoreBusConsumer consumer = this.consumers.get(topicRef);
			
			if( consumer == null ) {
				consumer = new GGCoreBusConsumer(queue, topicRef, this.name, this.pollInterval, this.pollIntervalUnit, this.poolExecutor);
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
	public void registerProducer(GGContextSubscription subscription, String tenantId, String clusterId,
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
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "bus", "1.0.0", this.infos, this.manual);
	}

}
