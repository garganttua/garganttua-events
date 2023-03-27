package com.gtech.garganttua.core.processors;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtech.garganttua.core.engine.processors.GGProtocolOutProcessor;
import com.gtech.garganttua.core.spec.annotations.GGProcessor;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.interfaces.IGGProcessor;
import com.gtech.garganttua.core.spec.interfaces.IGGProducer;
import com.gtech.garganttua.core.spec.interfaces.IGGSplitStrategy;
import com.gtech.garganttua.core.spec.interfaces.IGGSubscription;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGConfigurationDecoder;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGProcessor(type="dynamicSplitter", version="1.0.0")
public class GGDynamicSplitterProcessor extends GGAbstractProcessor {

	@Getter
	private String configuration;
	private String strategyClassName;
	private String to;
	private Map<String, IGGSubscription> subscriptions = new HashMap<String, IGGSubscription>();
	private IGGSplitStrategy strategyObject;
	private String tenantId;
	private String clusterId;
	private String assetId;
	private boolean multithreaded = false;
	private String infos;
	private String manual;

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries) throws GGCoreException {
		this.configuration = configuration;
		this.tenantId = tenantId;
		this.clusterId = clusterId;
		this.assetId = assetId;

		Map<String, List<String>> __configuration__ = GGConfigurationDecoder.getConfigurationFromString(configuration);
		
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
			throw new GGCoreException(e);
		}
		
		for( String key: to.keySet()) {
			String subId = to.get(key);
			IGGSubscription sub = this.contextEngine.getSubscription(subId, tenantId, clusterId);
			if( sub == null ) {
				throw new GGCoreException("Cannot configure dynamic splitter as subscription "+subId+" is not registered.");
			}
			
			sub.getConnector().registerProducer(sub.getSubscription(), tenantId, clusterId, assetId);

			this.subscriptions.put(key, sub);
		}
		
		try {
			Class<?> strategy = Class.forName(this.strategyClassName);
			if( IGGSplitStrategy.class.isAssignableFrom(strategy) ) {
				Constructor<?> ctor;
				try {
					ctor = strategy.getDeclaredConstructor();
				} catch (NoSuchMethodException | SecurityException e1) {
					throw new GGCoreException(e1);
				}
				try {
					this.strategyObject = (IGGSplitStrategy) ctor.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new GGCoreException(e);
				}
			} else {
				throw new GGCoreException(
						"The class [" + this.strategyClassName + "] must implements the IGGSplitStrategy interface.");
			}
			
		} catch (ClassNotFoundException e) {
			throw new GGCoreException(e);
		}
	}

	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		
		List<Entry<String, Entry<String, byte[]>>> splitted = this.strategyObject.split(exchange.getTenantId(), exchange.getValue(), this.subscriptions.keySet());
		for( Entry<String, Entry<String, byte[]>> entry: splitted ) {
			IGGSubscription sub = this.subscriptions.get(entry.getKey());
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
							} catch (GGCoreException e) {
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

	private void handleSplittedMessage(GGExchange exchange, byte[] message, String key, IGGSubscription sub, String contentType)
			throws GGCoreProcessingException, GGCoreException {
		GGExchange clone = exchange.clone();
		clone.setValue(message);
		clone.setContentType(contentType);

		GGProtocolOutProcessor protproc = new GGProtocolOutProcessor(sub.getDataflow().isEncapsulated(), this.assetId, this.clusterId, sub.getSubscription().getTopic(), sub.getDataflow().getVersion(), sub.getId() );
		IGGProcessor pproc = sub.getProducerProcessor();
		IGGProducer prod = sub.getProducer();

		protproc.handle(clone);
		pproc.handle(clone);
		prod.handle(clone);
	}

	@Override
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "dynamicSplitter", "1.0.0", this.infos, this.manual);
	}
}