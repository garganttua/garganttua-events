/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import com.garganttua.events.engine.processors.GGEventsFilterException;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.exceptions.GGEventsProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsConfigurable;
import com.garganttua.events.spec.interfaces.IGGEventsExceptionSubscription;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.IGGEventsProducer;
import com.garganttua.events.spec.interfaces.IGGEventsRoute;
import com.garganttua.events.spec.interfaces.IGGEventsSubscription;
import com.garganttua.events.spec.interfaces.IGGEventsTypable;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.objects.GGEventsConnectorConsumerRegistrationRequest;
import com.garganttua.events.spec.objects.GGEventsConnectorProducerRegistrationRequest;
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

	public GGEventsRoute(IGGEventsSubscription fromSubscription, IGGEventsSubscription toSubscription, IGGEventsSubscription exceptionSubscription, GGEventsLockObject lock, List<IGGEventsProcessor> processors, String routeUuid, String tenantId, String clusterId, String assetId) {
		this.lock = lock;
		this.exceptionSubscription = (IGGEventsExceptionSubscription) exceptionSubscription;
		this.routeUuid = routeUuid;
		this.clusterId = clusterId;
		this.assetId = assetId;
		this.tenantId = tenantId;
		this.processorsList = new GGEventsSynchronizedLinkedProcessorList();
		this.exceptionList = new GGEventsSynchronizedLinkedProcessorList();
		
		IGGEventsContextDataflow fromDataflow = ((GGEventsDataflow) fromSubscription.getDataflow()).getDataflow();
		
		fromSubscription.getConsumer().registerRoute(this);
		fromSubscription.getConnector().registerConsumer(new GGEventsConnectorConsumerRegistrationRequest(fromDataflow, fromSubscription.getSubscription(), this));
		
		this.processorsList.add(fromSubscription.getProtocolInProcessor());
		this.processorsList.add(fromSubscription.getInFilterProcessor());

		processors.forEach(proc -> {
			processorsList.add(proc);
		});
		
		if( toSubscription != null ) {
			IGGEventsContextDataflow toDataflow = ((GGEventsDataflow) toSubscription.getDataflow()).getDataflow();
			this.processorsList.add(toSubscription.getOutFilterProcessor());
			this.processorsList.add(toSubscription.getProtocolOutProcessor());
			this.producer = toSubscription.getProducer();
			this.processorsList.add(this.producer);
			toSubscription.getConnector().registerProducer(new GGEventsConnectorProducerRegistrationRequest(toDataflow, toSubscription.getSubscription()));
		}
		if( exceptionSubscription != null ) {
			IGGEventsContextDataflow exceptionDataflow = ((GGEventsDataflow) exceptionSubscription.getDataflow()).getDataflow();
			this.exceptionList.add(exceptionSubscription.getOutFilterProcessor());
			this.exceptionList.add(toSubscription.getProtocolOutProcessor());
			this.exceptionList.add(exceptionSubscription.getProducer());
			exceptionSubscription.getConnector().registerProducer(new GGEventsConnectorProducerRegistrationRequest(exceptionDataflow, exceptionSubscription.getSubscription()));
		}	
	}

	@Override
	public boolean handle(GGEventsExchange message) throws GGEventsHandlingException {
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
					throw new GGEventsHandlingException(e);
				}
			
				Object[] args = {message, this.processorsList, uuid};
				this.lock.doSynchronously(this, method, args);

			} else {		
				this.handle(message, this.processorsList, uuid);
			}
		} catch (GGEventsFilterException e) {
			this.flush(this.processorsList, uuid);
			log.info("Message dropped by filter : "+e.getMessage());
		} catch (GGEventsHandlingException e) {
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
								throw new GGEventsHandlingException(e1);
							}
							try {
								obj = ctor.newInstance(e, this.exceptionSubscription.getLabel()==null?e.getMessage():this.exceptionSubscription.getLabel());
							} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
									| InvocationTargetException e1) {
								throw new GGEventsHandlingException(e1);
							}
							message.setException((GGEventsException) obj);
						
					} catch (ClassNotFoundException e2) {
						throw new GGEventsHandlingException(e2);
					}
				} else {
//					message.setException(e);
				}
//				e.setClazz(e.getClass().getName());
				try {
					this.handle(message, this.exceptionList, uuidEx);
				} catch (GGEventsHandlingException e1) {
					throw new GGEventsHandlingException(e1);
				}
			}
			throw e; 
		} catch (GGEventsProcessingException e) {
			throw new GGEventsHandlingException(e);
		}
		return true;
	}
	
	private void flush(GGEventsSynchronizedLinkedProcessorList processorsList, UUID uuid) {
		IGGEventsMessageHandler processor;
		while( (processor = processorsList.pop(uuid)) != null ) {/*Nothing to do*/};
	}

	private void handle(GGEventsExchange message, GGEventsSynchronizedLinkedProcessorList processorsList, UUID uuid) throws GGEventsHandlingException {
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
			
			log.info("[TenantId:"+tenantId+"][ClusterId:"+clusterId+"][RouteId:"+this.routeUuid+"][ExchangeId:"+message.getExchangeId()+"][CorrelationId:"+corrId+"][MessageId:"+messageId+"] "+((IGGEventsTypable) processor).getType());
			if( processor.handle(message) != true ) {
				log.info("[TenantId:"+tenantId+"][ClusterId:"+clusterId+"][RouteId:"+this.routeUuid+"][ExchangeId:"+message.getExchangeId()+"][CorrelationId:"+corrId+"][MessageId:"+messageId+"] process stopped due to processor");
				break;
			};
		}
	}

	@Override
	public void stop() throws GGEventsException {
		if( this.producer != null ) {
			this.producer.stop();
		}
	}

	@Override
	public void start(ScheduledExecutorService scheduledExecutorService) throws GGEventsException {
		for( IGGEventsMessageHandler proc: this.processorsList.getList() ) { 
			if( proc instanceof IGGEventsConfigurable ) {
				log.info("[" + assetId + "][" + tenantId + "][" + clusterId + "] Configuring processor " + ((IGGEventsTypable) proc).getType());
				try {
					((IGGEventsConfigurable) proc).applyConfiguration();
				} catch (Exception e) {
					log.error("Unable to start Garganttua Framework", e);
					throw new GGEventsException(e);
				}
				log.debug("[" + assetId + "][" + tenantId + "][" + clusterId + "] Configured " + ((IGGEventsTypable) proc).getType());
			}
		}
		
		if( this.lock != null ) {
			this.lock.start();
		}
		if( this.producer != null ) {
			this.producer.start(scheduledExecutorService);
		}
	}

}
