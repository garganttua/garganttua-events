/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.garganttua.events.engine;

import java.util.UUID;

public interface IGGEventsSynchronizedLinkedList<T> {
	
	void add(T element);

	UUID createTransaction();
	
	T pop(UUID uuid);
	
	void flushTerminatedTransactions();
	
	
}
