package com.garganttua.events.spec.objects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;
import com.jayway.jsonpath.JsonPath;

public class GGEventsCoreExceptionDeserializer extends JsonDeserializer<GGEventsCoreException> {

	@Override
	public GGEventsCoreException deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		TreeNode node = p.getCodec().readTree(p);
		p.skipChildren();
		String value = node.toString();
		
		InputStream targetStream = new ByteArrayInputStream(value.getBytes());
		
		Object className = JsonPath.parse(targetStream).read("$.clazz");
		GGEventsCoreException exception = null;
		try {
			Class<?> clazz = Class.forName((String) className);
			
			exception = (GGEventsCoreException) mapper.readValue(value.getBytes(), clazz);

		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
		
		return exception;
	}

}
