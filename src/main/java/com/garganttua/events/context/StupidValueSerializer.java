package com.garganttua.events.context;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class StupidValueSerializer extends JsonSerializer<String> {

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//		gen.writeStartObject();
//		gen.writeStringField("configuration", value);
//		gen.writeEndObject();
		gen.writeString(value);
	}
}
