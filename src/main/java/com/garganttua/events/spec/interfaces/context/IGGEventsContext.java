package com.garganttua.events.spec.interfaces.context;

import com.garganttua.events.context.GGEventsContextPublicationMode;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;

public interface IGGEventsContext {

	public IGGEventsEngine build();

	public IGGEventsContext topic(String ref);

	public IGGEventsContext dataflow(String uuid, String name, String type, boolean garanteeOrder, String version,
			boolean encapsulated);

	public IGGEventsContextSubscription subscription(String dataflowUuid, String topicRef, String connectorName,
			GGEventsContextPublicationMode publicationMode);

	public IGGEventsContext connector(String name, String type, String version, String configuration);

	public IGGEventsContextRoute route(String string, String string2, String string3);

	public IGGEventsContext lock(String name, String type, String version, String configuration);

	public String getTenantId();

	public String getClusterId();

	public void merge(IGGEventsContext context);

}
