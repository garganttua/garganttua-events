package com.gtech.garganttua.core.connectors.core.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtech.garganttua.core.context.GGContextSubscription;
import com.gtech.garganttua.core.spec.annotations.GGConnector;
import com.gtech.garganttua.core.spec.enums.GGCoreEventCriticity;
import com.gtech.garganttua.core.spec.enums.GGCoreExecutionStage;
import com.gtech.garganttua.core.spec.exceptions.GGConnectorException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreException;
import com.gtech.garganttua.core.spec.exceptions.GGCoreProcessingException;
import com.gtech.garganttua.core.spec.interfaces.IGGConnector;
import com.gtech.garganttua.core.spec.interfaces.IGGCoreEventHandler;
import com.gtech.garganttua.core.spec.interfaces.IGGMessageHandler;
import com.gtech.garganttua.core.spec.interfaces.IGGObjectRegistryHub;
import com.gtech.garganttua.core.spec.objects.GGConfigurationDecoder;
import com.gtech.garganttua.core.spec.objects.GGContextObjDescriptor;
import com.gtech.garganttua.core.spec.objects.GGCoreEvent;
import com.gtech.garganttua.core.spec.objects.GGExchange;
import com.gtech.garganttua.core.spec.objects.GGMessage;
import com.gtech.garganttua.core.spec.objects.GGRJourneyStep;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGConnector(type="events", version="1.0.0")
public class GGCoreEventsConnector implements IGGConnector, IGGCoreEventHandler {
	
	private static final String EVENT_EXECUTION_STAGE = "stage";
	private static final String EVENT_CRITICITY = "criticity";
	private static final String EVENT_MESSAGE = "message";
	
	@Getter
	@Setter
	private String name;
	private String tenantId;
	private String assetId;
	private String clusterId;
	private Map<IGGMessageHandler, GGContextSubscription> handlers = new HashMap<IGGMessageHandler, GGContextSubscription>();
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
	public void handle(GGExchange exchange) throws GGCoreProcessingException, GGCoreException {

	}

	@Override
	public String getType() {
		return "IGGConnector::events";
	}

	@Override
	public String getConfiguration() {
		return this.configuration;
	}

	@Override
	public void setConfiguration(String configuration, String tenantId, String clusterId, String assetId,
			IGGObjectRegistryHub objectRegistries) throws GGCoreException {
		this.configuration = configuration;
	}

	@Override
	public void applyConfiguration() throws GGCoreException {
		Map<String, List<String>> __configuration__ = GGConfigurationDecoder.getConfigurationFromString(this.configuration);
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
	public GGContextObjDescriptor getDescriptor() {
		return new GGContextObjDescriptor(this.getClass().getCanonicalName(), "events", "1.0.0", this.infos, this.manual);
	}

	@Override
	public void setPoolExecutor(ExecutorService poolExecutor) {
		this.poolExecutor = poolExecutor;
	}

	@Override
	public void start() throws GGConnectorException {
	}

	@Override
	public void stop() throws GGConnectorException {
	}

	@Override
	public void registerConsumer(GGContextSubscription subscription, IGGMessageHandler messageHandler, String tenantId,
			String clusterId, String assetId) {
		this.tenantId = tenantId;
		this.assetId = assetId;
		this.clusterId = clusterId;
		
		this.handlers.put(messageHandler, subscription);
	}

	@Override
	public void registerProducer(GGContextSubscription subscription, String tenantId, String clusterId,
			String assetId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(GGCoreEvent event) {
		
		GGCoreEventCriticity criticity = event.getCriticity();
		GGCoreExecutionStage stage = event.getStage();
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
				GGMessage message = new GGMessage(new HashMap<String, String>(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), new ArrayList<GGRJourneyStep>(), this.tenantId, bytes, MediaType.APPLICATION_JSON_TYPE.toString(), null, null);

				__bytes__ = mapper.writeValueAsString(message).getBytes();
				
				this.handlers.forEach((handler, sub) -> {
					GGExchange exchange = GGExchange.emptyExchange(this.name, sub.getTopic(), sub.getDataFlow(), __bytes__);
	
					Thread t = new Thread() {
						
						public void run(){
							try {
								handler.handle(exchange);
							} catch (GGCoreException e) {
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
