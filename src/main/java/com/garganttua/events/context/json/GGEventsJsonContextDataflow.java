package com.garganttua.events.context.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.garganttua.events.context.GGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextDataflow;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextItemBinder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GGEventsJsonContextDataflow implements IGGEventsContextItemBinder<IGGEventsContextDataflow> {
	
	@JsonInclude
	protected String uuid;
	
	@JsonInclude
	protected String name;
	
	@JsonInclude
	protected String type;
	
	@JsonInclude
	protected boolean garanteeOrder;
	
	@JsonInclude
	protected String version;
	
	@JsonInclude
	protected boolean encapsulated;

	@Override
	public IGGEventsContextDataflow bind() {
		return new GGEventsContextDataflow(this.uuid, this.name, this.type, this.garanteeOrder, this.version, this.encapsulated);
	}

	@Override
	public void build(IGGEventsContextDataflow bound) {
		this.uuid = bound.getUuid();
		this.name = bound.getName();
		this.type = bound.getType();
		this.garanteeOrder = bound.isGaranteeOrder();
		this.version = bound.getVersion();
		this.encapsulated = bound.isEncapsulated();
	}

}
