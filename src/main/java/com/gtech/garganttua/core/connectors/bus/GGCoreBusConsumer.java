package com.gtech.garganttua.core.connectors.bus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;
import com.gtech.garganttua.core.spec.objects.GGExchange;
import com.leansoft.bigqueue.BigQueueImpl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class GGCoreBusConsumer {

	private BigQueueImpl queue;
	private String topicRef;
	private String name;
	private Integer pollInterval;
	private TimeUnit pollIntervalUnit;
	private ExecutorService poolExecutor;
	
	private Map<String, IGGMessageHandler> handlers = new HashMap<String, IGGMessageHandler>();
	private Map<String, Boolean> garanteeOrder = new HashMap<String, Boolean>();
	
	public void start() {
		this.poolExecutor.execute(new Thread() {
			@Override
			public void run() {
				exec();
			}
		});
	}

	public void stop() {
		
	}
	
	public void exec() {
		while(true) {
			
			long time = TimeUnit.MILLISECONDS.convert(this.pollInterval, this.pollIntervalUnit);
			
			try {
				Thread.sleep(time);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if( this.queue.isEmpty() ) {
				continue;
			}
			
			while( this.queue.isEmpty() != true ) {
				try {
					byte[] bytes = this.queue.dequeue();
					
					try {
						GGBusMessage message = GGBusMessage.fromBytes(bytes);

						this.handlers.forEach((d,h)-> {
							if( d.equals(message.getToDataflowUuid()) ) {
								if( this.garanteeOrder.get(d) ) {
									GGExchange m = GGExchange.emptyExchange(this.name, this.topicRef, d, message.getValue());
									try {
										h.handle(m);
									} catch (GGCoreException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									this.poolExecutor.execute(new Thread() {

										public void run() {
											GGExchange m = GGExchange.emptyExchange(name, topicRef, d, message.getValue());
											try {
												h.handle(m);
											} catch (GGCoreException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									});
								}
							}
						});
					} catch(Exception e) {
						log.warn("Unable to decode the received message, dropping", e);
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void registerHandler(String dataflowUuid, IGGMessageHandler handler) {
		this.handlers.put(dataflowUuid, handler);
	}

	public GGCoreBusConsumer(BigQueueImpl queue, String topicRef, String name, Integer pollInterval, TimeUnit pollIntervalUnit, ExecutorService poolExecutor) {
		this.queue = queue;
		this.topicRef = topicRef;
		this.name = name;
		this.pollInterval = pollInterval;
		this.pollIntervalUnit = pollIntervalUnit;
		this.poolExecutor = poolExecutor;
	}

	public void setGaranteeOrder(String dataflowUuid, boolean b) {
		this.garanteeOrder.put(dataflowUuid, b);
	}

}
