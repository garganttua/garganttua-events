package com.garganttua.events.context.json;

import com.garganttua.events.context.GGEventsContextExceptions;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextExceptions;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GGEventsJsonContextExceptions implements IGGEventsContextItemBinder<IGGEventsContextExceptions> {


	private String to;
	

	private String cast;


	private String label;
	
	@Override
	public IGGEventsContextExceptions bind() throws GGEventsException {
		return new GGEventsContextExceptions(this.to, this.cast, this.label);
	}

	@Override
	public void build(IGGEventsContextExceptions contextItem) throws GGEventsException {
		this.to = contextItem.getTo();
		this.cast = contextItem.getCast();
		this.label = contextItem.getLabel();
	}

}
