package com.garganttua.events.spec.objects;

import com.garganttua.events.spec.enums.GGEventsEventCriticity;
import com.garganttua.events.spec.enums.GGEventsExecutionStage;
import com.garganttua.events.spec.exceptions.GGEventsException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GGEventsEvent {

	private String message;
	
	private GGEventsEventCriticity criticity;
	
	private GGEventsExecutionStage stage;

	private GGEventsException exception;
	
}
