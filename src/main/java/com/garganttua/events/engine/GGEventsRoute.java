/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.engine.processors.GGEventsCoreFilterException;
import com.garganttua.events.engine.processors.GGEventsProtocolInProcessor;
import com.garganttua.events.engine.processors.GGEventsProtocolOutProcessor;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsExceptionSubscription;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.IGGEventsProducer;
import com.garganttua.events.spec.interfaces.IGGEventsRoute;
import com.garganttua.events.spec.interfaces.IGGEventsSubscription;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GGEventsRoute implements IGGEventsRoute {

	private GGEventsSynchronizedLinkedProcessorList processorsList;
	private GGEventsSynchronizedLinkedProcessorList exceptionList;
	private String routeUuid;
	private String clusterId;
	private String assetId;
	private IGGEventsExceptionSubscription exceptionSubscription;
	private IGGEventsProducer producer;
	private GGEventsLockObject lock;

	public GGEventsRoute(IGGEventsSubscription fromSubscription, IGGEventsSubscription toSubscription, IGGEventsSubscription exceptionSubscription, GGEventsLockObject lock, Map<Integer, IGGEventsProcessor> processors, String routeUuid, String clusterId, String assetId) {
		this.lock = lock;
		this.exceptionSubscription = (IGGEventsExceptionSubscription) exceptionSubscription;
		this.routeUuid = routeUuid;
		this.clusterId = clusterId;
		this.assetId = assetId;
		this.processorsList = new GGEventsSynchronizedLinkedProcessorList();
		this.exceptionList = new GGEventsSynchronizedLinkedProcessorList();
		
		ArrayList<Integer> list = new ArrayList<Integer>(processors.keySet());
		Collections.sort(list, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if(o1 > o2)
					return 1;
				if(o1 == o2)
					return 0;
				if(o1 < o2)
					return -1;
				return 0;
			}
		});
		
		this.processorsList.add(new GGEventsProtocolInProcessor(fromSubscription.getDataflow().isEncapsulated(), this.assetId, this.clusterId, fromSubscription.getId(), fromSubscription.getDataflow().getVersion()));
		if( fromSubscription.getDataflow().isEncapsulated() )
			this.processorsList.add(fromSubscription.getConsumerProcessor());
		list.forEach(integer -> {
			IGGEventsProcessor proc = processors.get(integer);
			processorsList.add(proc);
		});
		
		if( toSubscription != null ) {
			this.processorsList.add(new GGEventsProtocolOutProcessor(toSubscription.getDataflow().isEncapsulated(), this.assetId, this.clusterId, toSubscription.getSubscription().getTopic(), toSubscription.getDataflow().getVersion(), toSubscription.getId() ));
			this.processorsList.add(toSubscription.getProducerProcessor());
			this.producer = toSubscription.getProducer();
			this.processorsList.add(this.producer);
		}
		if( exceptionSubscription != null ) {
			this.exceptionList.add(exceptionSubscription.getProducerProcessor());
			this.exceptionList.add(new GGEventsProtocolOutProcessor(exceptionSubscription.getDataflow().isEncapsulated(), this.assetId, this.clusterId, exceptionSubscription.getSubscription().getTopic(), exceptionSubscription.getDataflow().getVersion(), exceptionSubscription.getId() ));
			this.exceptionList.add(exceptionSubscription.getProducer());
		}
			
	}

	@Override
	public void handle(GGEventsExchange message) throws GGEventsCoreException, GGEventsCoreProcessingException {
		String tenantId = message.getTenantId()==null?"unknown":message.getTenantId();
		String clusterId = message.getSteps().size()==0?"unknown":message.getSteps().get(message.getSteps().size()-1).getClusterId();
		String messageId = message.getSteps().size()==0?"unknown":message.getSteps().get(message.getSteps().size()-1).getUuid();
		String corrId = message.getCorrelationId();
		log.info("[TenantId:"+tenantId+"][ClusterId:"+clusterId+"][RouteId:"+this.routeUuid+"][ExchangeId:"+message.getExchangeId()+"][CorrelationId:"+corrId+"][MessageId:"+messageId+"] Routing message");
		
		UUID uuid = this.processorsList.createTransaction();
		
		try {
			
			if( this.lock != null ) {
				
				Method method = null;
				try {
					method = this.getClass().getDeclaredMethod("handle", GGEventsExchange.class, GGEventsSynchronizedLinkedProcessorList.class, UUID.class);
				} catch (NoSuchMethodException | SecurityException e) {
					throw new GGEventsCoreException(e);
				}
			
				Object[] args = {message, this.processorsList, uuid};
				this.lock.doSynchronously(this, method, args);

			} else {		
				this.handle(message, this.processorsList, uuid);
			}
		} catch (GGEventsCoreFilterException e) {
			this.flush(this.processorsList, uuid);
			log.info("Message dropped by filter : "+e.getMessage());
		} catch (GGEventsCoreException e) {
			this.flush(this.processorsList, uuid);
			if( this.exceptionSubscription != null ) {
				UUID uuidEx = this.exceptionList.createTransaction();
				
				if( this.exceptionSubscription.getCast() != null ) {
					String className = this.exceptionSubscription.getCast();
					
					Object obj = null;
					try {
						Class<?> clazz = Class.forName(className);
							Constructor<?> ctor;
							try {
								ctor = clazz.getDeclaredConstructor(Exception.class, String.class);
							} catch (NoSuchMethodException | SecurityException e1) {
								throw new GGEventsCoreException(e1);
							}
							try {
								obj = ctor.newInstance(e, this.exceptionSubscription.getLabel()==null?e.getMessage():this.exceptionSubscription.getLabel());
							} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
									| InvocationTargetException e1) {
								throw new GGEventsCoreException(e1);
							}
							message.setException((GGEventsCoreException) obj);
						
					} catch (ClassNotFoundException e2) {
						throw new GGEventsCoreException(e2);
					}
				} else {
					message.setException(e);
				}
				e.setClazz(e.getClass().getName());
				this.handle(message, this.exceptionList, uuidEx);
			}
			throw e; 
		}
	}
	
	private void flush(GGEventsSynchronizedLinkedProcessorList processorsList, UUID uuid) {
		IGGEventsMessageHandler processor;
		while( (processor = processorsList.pop(uuid)) != null ) {/*Nothing to do*/};
	}

	private void handle(GGEventsExchange message, GGEventsSynchronizedLinkedProcessorList processorsList, UUID uuid) throws GGEventsCoreProcessingException, GGEventsCoreException {
		String tenantId;
		String clusterId;
		String messageId;
		String corrId;
		IGGEventsMessageHandler processor;
	
		while( (processor = processorsList.pop(uuid)) != null ) {
			tenantId = message.getTenantId()==null?"unknown":message.getTenantId();
			clusterId = message.getSteps().size()==0?"unknown":message.getSteps().get(message.getSteps().size()-1).getClusterId();
			messageId = message.getSteps().size()==0?"unknown":message.getSteps().get(message.getSteps().size()-1).getUuid();
			corrId = message.getCorrelationId();
			
			log.debug("[TenantId:"+tenantId+"][ClusterId:"+clusterId+"][RouteId:"+this.routeUuid+"][ExchangeId:"+message.getExchangeId()+"][CorrelationId:"+corrId+"][MessageId:"+messageId+"] "+processor.getType());
			processor.handle(message);
		}
	}


	@Override
	public String getType() {
		return "IGGEventsRoute::GGEventsRoute";
	}

	@Override
	public void stop() throws GGEventsCoreException {
		if( this.producer != null ) {
			this.producer.stop();
		}
	}

	@Override
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGEventsCoreException {
		if( this.lock != null ) {
			this.lock.start();
		}
		if( this.producer != null ) {
			this.producer.start(scheduledExecutorService);
		}
	}

}
