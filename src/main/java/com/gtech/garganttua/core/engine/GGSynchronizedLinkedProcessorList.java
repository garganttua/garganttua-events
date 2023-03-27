/*******************************************************************************
 * Copyright (c) 2022 Jérémy COLOMBET
 *******************************************************************************/
package com.gtech.garganttua.core.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;

public class GGSynchronizedLinkedProcessorList implements IGGSynchronizedLinkedList<IGGMessageHandler> {
	
	private LinkedList<IGGMessageHandler> list = new LinkedList<IGGMessageHandler>();
	
	private Map<UUID, Integer> indexes = new ConcurrentHashMap<UUID, Integer>();

	@Override
	public UUID createTransaction() {
		UUID uuid = UUID.randomUUID();
		this.indexes.put(uuid, 0);
		return uuid;
	}

	@Override
	public IGGMessageHandler pop(UUID uuid) {
		Integer index = this.indexes.get(uuid);
		
		if(index == -1) {
			return null;
		}
		
		IGGMessageHandler proc = this.list.get(index);
		
		index++;
		
		if(index >= this.list.size()) {
			this.indexes.replace(uuid, -1);
		} else {
			this.indexes.replace(uuid, index);
		}
		
		return proc;
	}

	@Override
	public void flushTerminatedTransactions() {
		ArrayList<UUID> indexesToBeRemoved = new ArrayList<UUID>();
		this.indexes.forEach( (k,v) -> {
			if( v == -1 ) {
				indexesToBeRemoved.add(k);
			}
		});
		indexesToBeRemoved.forEach( uuid -> {
			indexes.remove(uuid);
		});
	}

	@Override
	public void add(IGGMessageHandler element) {
		if( element != null )
			this.list.addLast(element);
	}

}
