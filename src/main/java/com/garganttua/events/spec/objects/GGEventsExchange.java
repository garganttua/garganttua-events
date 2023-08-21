/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.spec.objects;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.jayway.jsonpath.JsonPath;

import lombok.Getter;
import lombok.Setter;

public class GGEventsExchange extends GGEventsMessage {
	
	@Getter 
	@Setter
	private String toTopic;
	
	@Getter 
	@Setter
	private String toConnector;
	
	@Getter 
	@Setter
	private String toDataflowUuid;
	
	@Getter 
	@Setter
	private String fromTopic;
	
	@Getter 
	@Setter
	private String fromConnector;
	
	@Getter 
	@Setter
	private String fromDataflowUuid;
	
	
	@Getter
	private String exchangeId = UUID.randomUUID().toString();

	private GGEventsExchange(String fromConnector, String fromTopic, String fromDataflowUuid, byte[] message) {
		this.fromConnector = fromConnector;
		this.fromTopic = fromTopic;
		this.fromDataflowUuid = fromDataflowUuid;
		this.value = message;
		this.steps = new ArrayList<GGEventsJourneyStep>();
		this.headers = new HashMap<String, String>();
	}

	private GGEventsExchange(String fromConnector, String fromTopic, String fromDataflowUuid, GGEventsMessage message) {
		this.fromConnector = fromConnector;
		this.fromTopic = fromTopic;
		this.fromDataflowUuid = fromDataflowUuid;
		this.correlationId = message.correlationId;
		this.messageId = message.messageId;
		this.steps = message.steps;
		this.tenantId = message.tenantId;
		this.value = message.value;
		this.headers = message.headers;
		this.exception = message.getException();
		this.contentType = message.getContentType();
		this.toUuid = message.toUuid;
	}

	public static GGEventsExchange emptyExchange(String fromConnector, String fromTopic, String fromDataflowUuid, byte[] message) {
		return new GGEventsExchange(fromConnector, fromTopic, fromDataflowUuid, message);
	}

	public GGEventsMessage toGGEventsMessage() {
		GGEventsMessage message = new GGEventsMessage();
		message.correlationId = this.correlationId;
		message.steps = this.steps;
		message.tenantId = this.tenantId;
		message.value = this.value;
		message.headers = this.headers;
		message.messageId = this.messageId;
		message.exception = this.exception;
		message.toUuid = this.toUuid;
		message.contentType = this.contentType;
		message.dataflowVersion = this.dataflowVersion;
		return message;
	}
	
	public GGEventsExchange clone() {
		GGEventsExchange exchange = new GGEventsExchange(this.fromConnector, this.fromTopic, this.fromDataflowUuid, this.toGGEventsMessage());
		return exchange;
	}

	public String getVariableValue(GGEventsExchange exchange, String variable) {
		String valueVar = variable.substring(2, variable.length()-1);
		
		String value = "";
		
		String[] varSplit = valueVar.split("\\.");
		if( varSplit[0].equals("exchange") ) {
			if( varSplit[1].equals("steps") ) {
				if( varSplit[2].equals("assetId") ) {
					value = exchange.getSteps().get(0).getAssetId();
				}
			}
			if( varSplit[1].equals("value") ) {
				if( varSplit[2].startsWith("json") ) {
					String[] toto = variable.split("\\(");
					String[] toto2 = toto[1].split("\\)");

					InputStream targetStream = new ByteArrayInputStream(exchange.getValue());
											
					Object t = JsonPath.parse(targetStream).read(toto2[0]);
					
					if( t instanceof String ) {
						value = (String) t;
					} 
					if( t instanceof Long ) {
						value = Long.toString((long) t);
					}
				}
			}
			if( varSplit[1].equals("headers") ) {
				String headerName = varSplit[2];
				value = exchange.getHeaders().get(headerName);
			}
			if( varSplit[1].equals("tenantId") ) {
				value = exchange.getTenantId();
			}
		}
		return value;
	}

	public boolean isVariable(String value) {
		return value.startsWith("${") && value.endsWith("}");
	}

}
