/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import com.gtech.garganttua.core.engine.processors.GGCoreFilterException;
import com.gtech.garganttua.core.engine.processors.GGProtocolInProcessor;
import com.gtech.garganttua.core.engine.processors.GGProtocolOutProcessor;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGExceptionSubscription;
import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;
import com.gtech.garganttua.core.spec.interfaces.IGGProcessor;
import com.gtech.garganttua.core.spec.interfaces.IGGProducer;
import com.gtech.garganttua.core.spec.interfaces.IGGRoute;
import com.gtech.garganttua.core.spec.interfaces.IGGSubscription;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GGRoute implements IGGRoute {

	private GGSynchronizedLinkedProcessorList processorsList;
	private GGSynchronizedLinkedProcessorList exceptionList;
	private String routeUuid;
	private String clusterId;
	private String assetId;
	private IGGExceptionSubscription exceptionSubscription;
	private IGGProducer producer;
	private GGLockObject lock;

	public GGRoute(IGGSubscription fromSubscription, IGGSubscription toSubscription, IGGSubscription exceptionSubscription, GGLockObject lock, Map<Integer, IGGProcessor> processors, String routeUuid, String clusterId, String assetId) {
		this.lock = lock;
		this.exceptionSubscription = (IGGExceptionSubscription) exceptionSubscription;
		this.routeUuid = routeUuid;
		this.clusterId = clusterId;
		this.assetId = assetId;
		this.processorsList = new GGSynchronizedLinkedProcessorList();
		this.exceptionList = new GGSynchronizedLinkedProcessorList();
		
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
		
		this.processorsList.add(new GGProtocolInProcessor(fromSubscription.getDataflow().isEncapsulated(), this.assetId, this.clusterId, fromSubscription.getId(), fromSubscription.getDataflow().getVersion()));
		if( fromSubscription.getDataflow().isEncapsulated() )
			this.processorsList.add(fromSubscription.getConsumerProcessor());
		list.forEach(integer -> {
			IGGProcessor proc = processors.get(integer);
			processorsList.add(proc);
		});
		
		if( toSubscription != null ) {
			this.processorsList.add(new GGProtocolOutProcessor(toSubscription.getDataflow().isEncapsulated(), this.assetId, this.clusterId, toSubscription.getSubscription().getTopic(), toSubscription.getDataflow().getVersion(), toSubscription.getId() ));
			this.processorsList.add(toSubscription.getProducerProcessor());
			this.producer = toSubscription.getProducer();
			this.processorsList.add(this.producer);
		}
		if( exceptionSubscription != null ) {
			this.exceptionList.add(exceptionSubscription.getProducerProcessor());
			this.exceptionList.add(new GGProtocolOutProcessor(exceptionSubscription.getDataflow().isEncapsulated(), this.assetId, this.clusterId, exceptionSubscription.getSubscription().getTopic(), exceptionSubscription.getDataflow().getVersion(), exceptionSubscription.getId() ));
			this.exceptionList.add(exceptionSubscription.getProducer());
		}
			
	}

	@Override
	public void handle(GGExchange message) throws GGCoreException, GGCoreProcessingException {
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
					method = this.getClass().getDeclaredMethod("handle", GGExchange.class, GGSynchronizedLinkedProcessorList.class, UUID.class);
				} catch (NoSuchMethodException | SecurityException e) {
					throw new GGCoreException(e);
				}
			
				Object[] args = {message, this.processorsList, uuid};
				this.lock.doSynchronously(this, method, args);

			} else {		
				this.handle(message, this.processorsList, uuid);
			}
		} catch (GGCoreFilterException e) {
			this.flush(this.processorsList, uuid);
			log.info("Message dropped by filter : "+e.getMessage());
		} catch (GGCoreException e) {
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
								throw new GGCoreException(e1);
							}
							try {
								obj = ctor.newInstance(e, this.exceptionSubscription.getLabel()==null?e.getMessage():this.exceptionSubscription.getLabel());
							} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
									| InvocationTargetException e1) {
								throw new GGCoreException(e1);
							}
							message.setException((GGCoreException) obj);
						
					} catch (ClassNotFoundException e2) {
						throw new GGCoreException(e2);
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
	
	private void flush(GGSynchronizedLinkedProcessorList processorsList, UUID uuid) {
		IGGMessageHandler processor;
		while( (processor = processorsList.pop(uuid)) != null ) {/*Nothing to do*/};
	}

	private void handle(GGExchange message, GGSynchronizedLinkedProcessorList processorsList, UUID uuid) throws GGCoreProcessingException, GGCoreException {
		String tenantId;
		String clusterId;
		String messageId;
		String corrId;
		IGGMessageHandler processor;
	
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
		return "IGGRoute::GGRoute";
	}

	@Override
	public void stop() throws GGCoreException {
		if( this.producer != null ) {
			this.producer.stop();
		}
	}

	@Override
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGCoreException {
		if( this.lock != null ) {
			this.lock.start();
		}
		if( this.producer != null ) {
			this.producer.start(scheduledExecutorService);
		}
	}

}
