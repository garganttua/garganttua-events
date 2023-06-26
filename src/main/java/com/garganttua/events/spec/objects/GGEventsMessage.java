/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.objects;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsMessage {
	
	@JsonProperty
	protected Map<String, String> headers;
	
	@JsonProperty
	protected String correlationId;
	
	@JsonProperty
	protected String messageId;
	
	@JsonProperty
	protected List<GGEventsRJourneyStep> steps;
	
	@JsonProperty
	protected String tenantId;

	@JsonProperty
	protected byte[] value;
	
	@JsonProperty
	protected String contentType = MediaType.APPLICATION_JSON_TYPE.toString();
	
//	@JsonProperty
//	@JsonDeserialize(using = GGEventsFrameworkExceptionDeserializer.class)
	@JsonIgnore
	protected GGEventsCoreException exception;

	@JsonProperty
	protected String toUuid;
}
