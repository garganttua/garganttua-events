/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.UUID;

public interface IGGSynchronizedLinkedList<T> {
	
	void add(T element);

	UUID createTransaction();
	
	T pop(UUID uuid);
	
	void flushTerminatedTransactions();
	
	
}
