package com.gtech.garganttua.core.spec.objects;

import com.gtech.garganttua.core.spec.enums.GGCoreEventCriticity;
import com.gtech.garganttua.core.spec.enums.GGCoreExecutionStage;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GGCoreEvent {

	private String message;
	
	private GGCoreEventCriticity criticity;
	
	private GGCoreExecutionStage stage;

	private GGCoreException exception;
	
}
