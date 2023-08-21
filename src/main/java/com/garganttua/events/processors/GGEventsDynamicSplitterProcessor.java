package com.garganttua.events.processors;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.garganttua.events.spec.exceptions.GGEventsCoreProcessingException;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.IGGEventsProducer;
import com.garganttua.events.spec.interfaces.IGGEventsSplitStrategy;
import com.garganttua.events.spec.interfaces.IGGEventsSubscription;
import com.garganttua.events.spec.objects.GGEventsAbstractProcessor;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGEventsProcessor(type="dynamicSplitter", version="1.0.0")
public class GGEventsDynamicSplitterProcessor extends GGEventsAbstractProcessor {

	@Getter
	private String configuration;
	private String strategyClassName;
	private String to;
	private Map<String, IGGEventsSubscription> subscriptions = new HashMap<String, IGGEventsSubscription>();
	private IGGEventsSplitStrategy strategyObject;
	private String tenantId;
	private String clusterId;
	private String assetId;
	private boolean multithreaded = false;
	private String infos;
	private String manual;

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries) throws GGEventsCoreException {
		this.configuration = configuration;
		this.tenantId = tenantId;
		this.clusterId = clusterId;
		this.assetId = assetId;

		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(configuration);
		
		__configuration__.forEach((k,v) -> {
			switch(k) {
			case "splitStrategy":
				strategyClassName = v.get(0);
				break;
			case "to":
				to = v.get(0);
				break;
			}
		});
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> to;
		try {
			to = mapper.readValue(this.to.getBytes(), Map.class);
		} catch (IOException e) {
			throw new GGEventsCoreException(e);
		}
		
		for( String key: to.keySet()) {
			String subId = to.get(key);
			IGGEventsSubscription sub = this.contextEngine.getSubscription(subId, tenantId, clusterId);
			if( sub == null ) {
				throw new GGEventsCoreException("Cannot configure dynamic splitter as subscription "+subId+" is not registered.");
			}
			
			sub.getConnector().registerProducer(sub.getSubscription(), tenantId, clusterId, assetId);

			this.subscriptions.put(key, sub);
		}
		
		try {
			Class<?> strategy = Class.forName(this.strategyClassName);
			if( IGGEventsSplitStrategy.class.isAssignableFrom(strategy) ) {
				Constructor<?> ctor;
				try {
					ctor = strategy.getDeclaredConstructor();
				} catch (NoSuchMethodException | SecurityException e1) {
					throw new GGEventsCoreException(e1);
				}
				try {
					this.strategyObject = (IGGEventsSplitStrategy) ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new GGEventsCoreException(e);
				}
			} else {
				throw new GGEventsCoreException(
						"The class [" + this.strategyClassName + "] must implements the IGGEventsSplitStrategy interface.");
			}
			
		} catch (ClassNotFoundException e) {
			throw new GGEventsCoreException(e);
		}
	}

	@Override
	public void handle(GGEventsExchange exchange) throws GGEventsCoreProcessingException, GGEventsCoreException {
		
		List<Entry<String, Entry<String, byte[]>>> splitted = this.strategyObject.split(exchange.getTenantId(), exchange.getValue(), this.subscriptions.keySet());
		for( Entry<String, Entry<String, byte[]>> entry: splitted ) {
			IGGEventsSubscription sub = this.subscriptions.get(entry.getKey());
			if( sub != null ) {
				if( !this.multithreaded ) {
					log.debug("key "+entry.getKey()+" value "+new String(entry.getValue().getValue()));
					this.handleSplittedMessage(exchange, entry.getValue().getValue(), entry.getKey(), sub, entry.getValue().getKey());
				} else {
					ExecutorService exec = this.contextEngine.getExecutorService();
					exec.execute(new Thread() {
						@Override
						public void run() {
							try {
								log.debug("key "+entry.getKey()+" value "+new String(entry.getValue().getValue()));
								handleSplittedMessage(exchange, entry.getValue().getValue(), entry.getKey(), sub, entry.getValue().getKey());
							} catch (GGEventsCoreException e) {
								log.warn("Unable to send splitted message to subscription "+sub.getId(), e);
							}
						}
					});
				}
				
			} else {
				log.warn("No subscription found for key "+entry.getKey()+". Ignoring.");
			}
		}
	}

	private void handleSplittedMessage(GGEventsExchange exchange, byte[] message, String key, IGGEventsSubscription sub, String contentType)
			throws GGEventsCoreProcessingException, GGEventsCoreException {
		GGEventsExchange clone = exchange.clone();
		clone.setValue(message);
		clone.setContentType(contentType);

		IGGEventsProcessor pproc = sub.getOutFilterProcessor();
		IGGEventsProcessor protproc = sub.getProtocolOutProcessor();
		IGGEventsProducer prod = sub.getProducer();

		protproc.handle(clone);
		pproc.handle(clone);
		prod.handle(clone);
	}

	@Override
	public void applyConfiguration() throws GGEventsCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "dynamicSplitter", "1.0.0", this.infos, this.manual);
	}
}