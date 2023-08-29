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

import com.garganttua.events.engine.processors.GGEventsFilterException;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
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
	private String tenantId;

	public GGEventsRoute(IGGEventsSubscription fromSubscription, IGGEventsSubscription toSubscription, IGGEventsSubscription exceptionSubscription, GGEventsLockObject lock, Map<Integer, IGGEventsProcessor> processors, String routeUuid, String tenantId, String clusterId, String assetId) {
		this.lock = lock;
		this.exceptionSubscription = (IGGEventsExceptionSubscription) exceptionSubscription;
		this.routeUuid = routeUuid;
		this.clusterId = clusterId;
		this.assetId = assetId;
		this.tenantId = tenantId;
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
		
		fromSubscription.getConsumer().registerRoute(this);
		fromSubscription.getConnector().registerConsumer(fromSubscription.getSubscription(), this, this.tenantId, this.clusterId, this.assetId);
		
		this.processorsList.add(fromSubscription.getProtocolInProcessor());
		this.processorsList.add(fromSubscription.getInFilterProcessor());

		list.forEach(integer -> {
			IGGEventsProcessor proc = processors.get(integer);
			processorsList.add(proc);
		});
		
		if( toSubscription != null ) {
			this.processorsList.add(toSubscription.getOutFilterProcessor());
			this.processorsList.add(toSubscription.getProtocolOutProcessor());
			this.producer = toSubscription.getProducer();
			this.processorsList.add(this.producer);
			toSubscription.getConnector().registerProducer(toSubscription.getSubscription(), this.tenantId, this.clusterId, this.assetId);
		}
		if( exceptionSubscription != null ) {
			this.exceptionList.add(exceptionSubscription.getOutFilterProcessor());
			this.processorsList.add(toSubscription.getProtocolOutProcessor());
			this.exceptionList.add(exceptionSubscription.getProducer());
			exceptionSubscription.getConnector().registerProducer(exceptionSubscription.getSubscription(), this.tenantId, this.clusterId, this.assetId);
		}	
	}

	@Override
	public void handle(GGEventsExchange message) throws GGEventsException, GGEventsProcessingException {
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
					throw new GGEventsException(e);
				}
			
				Object[] args = {message, this.processorsList, uuid};
				this.lock.doSynchronously(this, method, args);

			} else {		
				this.handle(message, this.processorsList, uuid);
			}
		} catch (GGEventsFilterException e) {
			this.flush(this.processorsList, uuid);
			log.info("Message dropped by filter : "+e.getMessage());
		} catch (GGEventsException e) {
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
								throw new GGEventsException(e1);
							}
							try {
								obj = ctor.newInstance(e, this.exceptionSubscription.getLabel()==null?e.getMessage():this.exceptionSubscription.getLabel());
							} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
									| InvocationTargetException e1) {
								throw new GGEventsException(e1);
							}
							message.setException((GGEventsException) obj);
						
					} catch (ClassNotFoundException e2) {
						throw new GGEventsException(e2);
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

	private void handle(GGEventsExchange message, GGEventsSynchronizedLinkedProcessorList processorsList, UUID uuid) throws GGEventsProcessingException, GGEventsException {
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
	public void stop() throws GGEventsException {
		if( this.producer != null ) {
			this.producer.stop();
		}
	}

	@Override
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGEventsException {
		if( this.lock != null ) {
			this.lock.start();
		}
		if( this.producer != null ) {
			this.producer.start(scheduledExecutorService);
		}
	}

}
