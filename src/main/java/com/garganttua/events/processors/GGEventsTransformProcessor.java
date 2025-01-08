package com.garganttua.events.processors;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.enums.GGEventsMediaType;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.interfaces.IGGEventsTransformer;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

@GGEventsProcessor(type = "transform", version="1.0")
public class GGEventsTransformProcessor implements IGGEventsProcessor {

	private static final String TO = "to";
	private static final String FROM = "from";
	private static final Object TRANSFORMER = "transformer";
	private String tenantId;
	private String configuration;
	private String clusterId;
	private String assetId;
	private String toClass;
	private String fromClass;
	private Class<?> fromClazz;
	private Class<?> toClazz;
	private String transformer;
	private Class<?> transformerClazz;
	private String infos;
	private String manual;
	private String type = "processor::transform";

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		
		byte[] fromByte = exchange.getValue();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Object fromObject = null;
			
			if( GGEventsMediaType.valueOf(exchange.getContentType()).toString().equals(GGEventsMediaType.TEXT_PLAIN.toString()) ) {
				fromObject = new String(fromByte);
			}
			if( GGEventsMediaType.valueOf(exchange.getContentType()).toString().equals(GGEventsMediaType.APPLICATION_JSON.toString()) ) {
				fromObject = mapper.readValue(fromByte, this.fromClazz);
			}
			
			Constructor<?> ctor;
			try {
				ctor = this.transformerClazz.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGEventsHandlingException(e1);
			}
			try {
				IGGEventsTransformer transformerObj = (IGGEventsTransformer) ctor.newInstance();
				
				Object toObject = transformerObj.transform(fromObject);
				byte[] toByte = mapper.writeValueAsBytes(toObject);
				exchange.setValue(toByte);
				
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGEventsHandlingException(e);
			}

		} catch (IOException | IllegalArgumentException | SecurityException e) {
			throw new GGEventsHandlingException(e);
		}
		return true;
	}

	@Override
	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine)
			throws GGEventsException {
		this.configuration = configuration;
		this.tenantId = tenantId;
		this.clusterId = clusterId;
		this.assetId = assetId;
		
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(configuration);
		
		__configuration__.forEach((k,v) -> {
			if( k.equals(TO) ) {
				this.toClass = v.get(0);
			}
			if( k.equals(FROM) ) {
				this.fromClass = v.get(0);
			}
			if( k.equals(TRANSFORMER) ) {
				this.transformer = v.get(0);
			}

		});
		
		try {
			this.fromClazz = Class.forName(this.fromClass);
			this.toClazz = Class.forName(this.toClass);
			this.transformerClazz = Class.forName(this.transformer);

		} catch (ClassNotFoundException e) {
			throw new GGEventsException(e);
		}
		
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "transform", "1.0", this.infos, this.manual);
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExecutorService(ExecutorService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScheduledExecutorService(ScheduledExecutorService service) {
		// TODO Auto-generated method stub
		
	}

}
