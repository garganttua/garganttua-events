package com.gtech.garganttua.core.processors;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtech.garganttua.core.spec.annotations.GGProcessor;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.interfaces.IGGTransformer;
import com.gtech.garganttua.core.spec.objects.GGAbstractProcessor;
import com.gtech.garganttua.core.spec.objects.GGConfigurationDecoder;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGExchange;

@GGProcessor(type = "transform", version="1.0.0")
public class GGTransformProcessor extends GGAbstractProcessor {

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

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {
		
		byte[] fromByte = exchange.getValue();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			Object fromObject = null;
			
			if( MediaType.valueOf(exchange.getContentType()).toString().equals(MediaType.TEXT_PLAIN.toString()) ) {
				fromObject = new String(fromByte);
			}
			if( MediaType.valueOf(exchange.getContentType()).toString().equals(MediaType.APPLICATION_JSON.toString()) ) {
				fromObject = mapper.readValue(fromByte, this.fromClazz);
			}
			
			Constructor<?> ctor;
			try {
				ctor = this.transformerClazz.getDeclaredConstructor();
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new GGCoreProcessingException(e1);
			}
			try {
				IGGTransformer transformerObj = (IGGTransformer) ctor.newInstance();
				
				Object toObject = transformerObj.transform(fromObject);
				byte[] toByte = mapper.writeValueAsBytes(toObject);
				exchange.setValue(toByte);
				
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new GGCoreException(e);
			}

		} catch (IOException | IllegalArgumentException | SecurityException e) {
			throw new GGCoreProcessingException(e);
		}
	}

	@Override
	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId, IGGObjectRegistryHub objectRegistries)
			throws GGCoreException {
		this.configuration = configuration;
		this.tenantId = tenantId;
		this.clusterId = clusterId;
		this.assetId = assetId;
		
		Map<String, List<String>> __configuration__ = GGConfigurationDecoder.getConfigurationFromString(configuration);
		
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
			throw new GGCoreException(e);
		}
		
	}

	@Override
	public void applyConfiguration() throws GGCoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "transform", "1.0.0", this.infos, this.manual);
	}

}
