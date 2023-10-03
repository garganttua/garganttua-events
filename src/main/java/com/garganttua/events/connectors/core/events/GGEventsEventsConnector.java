package com.garganttua.events.connectors.core.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.enums.GGEventsEventCriticity;
import com.garganttua.events.spec.enums.GGEventsExecutionStage;
import com.garganttua.events.spec.enums.GGEventsMediaType;
import com.garganttua.events.spec.exceptions.GGEventsConnectorException;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsEventHandler;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsEvent;
import com.garganttua.events.spec.objects.GGEventsExchange;
import com.garganttua.events.spec.objects.GGEventsJourneyStep;
import com.garganttua.events.spec.objects.GGEventsMessage;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGEventsConnector(type="events", version="1.0")
public class GGEventsEventsConnector implements IGGEventsConnector, IGGEventsEventHandler {
	
	private static final String EVENT_EXECUTION_STAGE = "stage";
	private static final String EVENT_CRITICITY = "criticity";
	private static final String EVENT_MESSAGE = "message";
	
	@Getter
	@Setter
	private String name;
	private String tenantId;
	private String assetId;
	private String clusterId;
	private Map<IGGEventsMessageHandler, IGGEventsContextSubscription> handlers = new HashMap<IGGEventsMessageHandler, IGGEventsContextSubscription>();
	private ExecutorService poolExecutor;
	private String configuration;
	private String infos;
	private String manual;
	private boolean allStages = false;
	private boolean allCriticities = false;
	private String[] stages;
	private String[] criticities;
	private String message;

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		return true;
	}

	@Override
	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId,
			IGGEventsObjectRegistryHub objectRegistries, IGGEventsEngine engine) throws GGEventsException {
		this.configuration = configuration;
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(this.configuration);
		__configuration__.forEach((name, values) -> { 
			switch(name) {
			case EVENT_EXECUTION_STAGE:
				if( values.get(0).equals("ANY") ) {
					this.allStages  = true;
				} else {
					this.stages = values.get(0).split(",");
				}
				break;
			case EVENT_CRITICITY:
				if( values.get(0).equals("ANY") ) {
					this.allCriticities   = true;
				} else {
					this.criticities = values.get(0).split(",");
				}
				break;
			case EVENT_MESSAGE:
				this.message = values.get(0);
				
				break;
			}
		});
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "events", "1.0.0", this.infos, this.manual);
	}

	@Override
	public void setPoolExecutor(ExecutorService poolExecutor) {
		this.poolExecutor = poolExecutor;
	}

	@Override
	public void start() throws GGEventsConnectorException {
	}

	@Override
	public void stop() throws GGEventsConnectorException {
	}

	@Override
	public void registerConsumer(IGGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler, String tenantId,
			String clusterId, String assetId) {
		this.tenantId = tenantId;
		this.assetId = assetId;
		this.clusterId = clusterId;
		
		this.handlers.put(messageHandler, subscription);
	}

	@Override
	public void registerProducer(IGGEventsContextSubscription subscription, String tenantId, String clusterId,
			String assetId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(GGEventsEvent event) {
		
		GGEventsEventCriticity criticity = event.getCriticity();
		GGEventsExecutionStage stage = event.getStage();
		boolean stageOk = false; 
		boolean criticityOk = false; 
		
		if( this.allStages ) {
			stageOk = true;
		} else {
			for( String st: this.stages ) {
				if( st.equals(stage.getLabel()) ) {
					stageOk = true;
					break;
				}
			}
		}
			
		if( this.allCriticities ) {
			criticityOk = true;
		} else {
			for( String crit: this.criticities ) {
				if( crit.equals(criticity.getLabel()) ) {
					criticityOk = true;
					break;
				}
			}
		}
	
		if( stageOk && criticityOk && this.message.equals(event.getMessage())) {
		
			ObjectMapper mapper = new ObjectMapper();
	
			byte[] bytes;
			byte[] __bytes__;
	
			try {
				bytes = mapper.writeValueAsString(event).getBytes();
				GGEventsMessage message = new GGEventsMessage(new HashMap<String, String>(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), new ArrayList<GGEventsJourneyStep>(), this.tenantId, bytes, GGEventsMediaType.APPLICATION_JSON.toString(), null, null, "1.0");

				__bytes__ = mapper.writeValueAsString(message).getBytes();
				
				this.handlers.forEach((handler, sub) -> {
					GGEventsExchange exchange = GGEventsExchange.emptyExchange(this.name, sub.getTopic(), sub.getDataflow(), __bytes__);
	
					Thread t = new Thread() {
						
						public void run(){
							try {
								handler.handle(exchange);
							} catch (GGEventsHandlingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					};
					this.poolExecutor.execute(t);
					
				});
	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
