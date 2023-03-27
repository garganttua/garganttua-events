/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.spec.objects;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGMessage {
	
	@JsonProperty
	protected Map<String, String> headers;
	
	@JsonProperty
	protected String correlationId;
	
	@JsonProperty
	protected String messageId;
	
	@JsonProperty
	protected List<GGRJourneyStep> steps;
	
	@JsonProperty
	protected String tenantId;

	@JsonProperty
	protected byte[] value;
	
	@JsonProperty
	protected String contentType = MediaType.APPLICATION_JSON_TYPE.toString();
	
//	@JsonProperty
//	@JsonDeserialize(using = GGFrameworkExceptionDeserializer.class)
	@JsonIgnore
	protected GGCoreException exception;

	@JsonProperty
	protected String toUuid;
}
