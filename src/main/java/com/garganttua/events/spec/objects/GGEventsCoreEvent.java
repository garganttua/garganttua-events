package com.garganttua.events.spec.objects;

import com.garganttua.events.spec.enums.GGEventsCoreEventCriticity;
import com.garganttua.events.spec.enums.GGEventsCoreExecutionStage;
import com.garganttua.events.spec.exceptions.GGEventsCoreException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GGEventsCoreEvent {

	private String message;
	
	private GGEventsCoreEventCriticity criticity;
	
	private GGEventsCoreExecutionStage stage;

	private GGEventsCoreException exception;
	
}
