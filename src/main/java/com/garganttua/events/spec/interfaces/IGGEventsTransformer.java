package com.garganttua.events.spec.interfaces;

public interface IGGEventsTransformer<T, U> {
	
	U transform(T in);

}
