package com.garganttua.events.spec.interfaces.context;

import java.util.List;

import com.garganttua.events.context.GGEventsContextPublicationMode;
import com.garganttua.events.spec.interfaces.IGGEventsBuilder;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsContextSource;

public interface IGGEventsContext {

	IGGEventsEngine build();

	IGGEventsContext topic(String ref);

	IGGEventsContext dataflow(String uuid, String name, String type, boolean garanteeOrder, String version, boolean encapsulated);

	IGGEventsContext connector(String name, String type, String version, String configuration);

	IGGEventsContextRoute route(String uuid, String from, String to);

	IGGEventsContext lock(String name, String type, String version, String configuration);

	IGGEventsContextSubscription subscription(String uuid, String topicRef, String connectorName, GGEventsContextPublicationMode publicationMode);

	String getTenantId();

	String getClusterId();

	IGGEventsBuilder builder();

	IGGEventsContext write(String sourceType, String version, String sourceConfiguration);

	IGGEventsContext write(IGGEventsContextSource source);
	
	IGGEventsContext lock(IGGEventsContextLock lock);

	IGGEventsContextRoute route(IGGEventsContextRoute route);

	IGGEventsContext connector(IGGEventsContextConnector connector);

	IGGEventsContextSubscription subscription(IGGEventsContextSubscription subscription);

	IGGEventsContext dataflow(IGGEventsContextDataflow dataflow);

	IGGEventsContext topic(IGGEventsContextTopic topic);
	
	List<IGGEventsContextTopic> getTopics();
	
	List<IGGEventsContextDataflow> getDataflows();
	
	List<IGGEventsContextSubscription> getSubscriptions();
	
	List<IGGEventsContextConnector> getConnectors();

	List<IGGEventsContextRoute> getRoutes();

	List<IGGEventsContextLock> getLocks();

	IGGEventsContext source(com.garganttua.events.spec.interfaces.context.IGGEventsContextSource source);

	List<com.garganttua.events.spec.interfaces.context.IGGEventsContextSource> getsources();

}
