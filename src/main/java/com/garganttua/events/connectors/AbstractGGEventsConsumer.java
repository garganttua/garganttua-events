package com.garganttua.events.connectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGGEventsConsumer implements Runnable {

	protected List<Pair<String, IGGEventsMessageHandler>> handlers = new ArrayList<Pair<String, IGGEventsMessageHandler>>();
	protected Map<String, Boolean> garanteeOrder = new HashMap<String, Boolean>();
	protected long pollInterval;
	protected TimeUnit pollIntervalUnit;
	protected String topicRef;
	protected String name;
	protected ExecutorService poolExecutor;

	public AbstractGGEventsConsumer(String topicRef, String name, Integer pollInterval, TimeUnit pollIntervalUnit, ExecutorService poolExecutor) {
		this.topicRef = topicRef;
		this.name = name;
		this.pollInterval = pollInterval;
		this.pollIntervalUnit = pollIntervalUnit;
		this.poolExecutor = poolExecutor;
	}
	
	public void registerHandler(String dataflowUuid, IGGEventsMessageHandler handler) {
		this.handlers.add(new Pair<String, IGGEventsMessageHandler>(dataflowUuid, handler));
	}

	public void setGaranteeOrder(String dataflowUuid, boolean garanteeOrder) {
		this.garanteeOrder.put(dataflowUuid, garanteeOrder);
	}

	@Override
	public void run() {
		log.info("[Connector:{}][Topic:{}] Starting", this.name, this.topicRef);
		while (true) {
			long timeout = TimeUnit.MILLISECONDS.convert(this.pollInterval, this.pollIntervalUnit);
			
			log.debug("[Connector:{}][Topic:{}][Timeout:{}] Waiting for messages.", this.name, this.topicRef, timeout);
			Map<String, byte[]> receivedMessages = this.doWaitForMessages(timeout);
			
			if( receivedMessages != null && !receivedMessages.isEmpty() ) {
				
				log.info("[Connector:{}][Topic:{}][Timeout:{}] Received {} messages", this.name, this.topicRef, timeout, receivedMessages.size());
				
				receivedMessages.forEach((dataflowUuid, message) -> {
					
					log.debug("[Connector:{}][Topic:{}][Timeout:{}] Dataflow {} : Received bytes {}", this.name, this.topicRef, timeout, dataflowUuid, message);
					
					this.handlers.forEach(pair -> {
						if( pair.object1().equals(dataflowUuid) ) {
							if( this.garanteeOrder.get(dataflowUuid) ) {
								GGEventsExchange m = GGEventsExchange.emptyExchange(this.name, this.topicRef, dataflowUuid, message);
								try {
									pair.object2().handle(m);
								} catch (GGEventsHandlingException e) {
									log.warn("[Connector:{}][Topic:{}][Timeout:{}] Error during message routing", this.name, this.topicRef, timeout, e.getMessage(), e);
								}
							} else {
								this.poolExecutor.execute(new Thread() {
									public void run() {
										GGEventsExchange m = GGEventsExchange.emptyExchange(name, topicRef, dataflowUuid, message);
										try {
											pair.object2().handle(m);
										} catch (GGEventsHandlingException e) {
											log.warn("[Connector:{}][Topic:{}][Timeout:{}] Error during message routing", name, topicRef, timeout, e.getMessage(), e);
										}
									}
								});
							}
						}
					});
				});
				this.ackMessages(receivedMessages);
			} else {
				log.debug("[Connector:{}][Topic:{}][Timeout:{}] No message received during wait time.", this.name, this.topicRef, timeout);
			}
		}
	}

	protected abstract void ackMessages(Map<String, byte[]> receivedMessages);

	protected abstract Map<String, byte[]> doWaitForMessages(long timeout);

	public void stop() {
		// TODO Auto-generated method stub

	}

}
