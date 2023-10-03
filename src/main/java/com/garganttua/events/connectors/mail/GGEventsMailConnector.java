package com.garganttua.events.connectors.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import com.garganttua.events.spec.annotations.GGEventsConnector;
import com.garganttua.events.spec.exceptions.GGEventsConnectorException;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsConnector;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsMessageHandler;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.context.IGGEventsContextSubscription;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import jakarta.mail.MessagingException;

@GGEventsConnector(type = "mail", version = "1.0")
public class GGEventsMailConnector implements IGGEventsConnector {

	private static final String FROM = "from";
	private static final String TO = "to";
	private static final String OBJECT = "object";
	private static final String SMTP_HOST = "host";
	private static final String SMTP_PORT = "port";
	private static final String SMTP_AUTH = "auth";
	private static final String SMTP_STARTTLS = "starttls";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String BODY = "body";
	private static final String SMTP_SSL = "ssl";
	private static final String CONTENT_TYPE = "contentType";
	private String name;
	private ExecutorService poolExecutor;
	private String configuration;
	private String tenantId;
	private String clusterId;
	private String assetId;
	private IGGEventsObjectRegistryHub objectRegistries;
	private IGGEventsEngine engine;
	private String from;
	private String object;
	private String userName;
	private String password;
	private String body;
	private String host;
	private String port;
	private String auth = "false";
	private String starttls = "false";
	private Properties properties;
	private Map<String, GGEventsMailSender> mailSenders = new HashMap<String, GGEventsMailSender>();
	private Object ssl;
	private String to;
	private String contentType;

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		try {
			this.mailSenders.get(exchange.getToTopic()).sendMail(exchange);
		} catch (MessagingException | GGEventsException e) {
			throw new GGEventsHandlingException(e);
		}
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
		this.tenantId = tenantId;
		this.clusterId = clusterId;
		this.assetId = assetId;
		this.objectRegistries = objectRegistries;
		this.engine = engine;
		
		
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder.getConfigurationFromString(configuration);
		__configuration__.forEach((name, values) -> { 
			switch(name) {
			case TO:
				this.to = values.get(0);
				break;
			case FROM:
				this.from = values.get(0);
				break;
			case OBJECT:
				this.object = values.get(0);
				break;
			case CONTENT_TYPE:
				this.contentType = values.get(0);
				break;
			case SMTP_HOST:
				this.host = values.get(0);
				break;
			case SMTP_PORT:
				this.port = values.get(0);
				break;
			case SMTP_AUTH:
				this.auth = values.get(0);
				break;
			case SMTP_STARTTLS:
				this.starttls = values.get(0);
				break;
			case SMTP_SSL:
				this.ssl = values.get(0);
				break;
			case USERNAME:
				this.userName = values.get(0);
				break;
			case PASSWORD:
				this.password = values.get(0);
				break;
			case BODY:
				this.body = values.get(0);
				break;
			}
		});
		
		if( this.userName == null || this.password == null || this.host == null || this.port == null || to == null) {
			throw new GGEventsException("Cannot configure mail connector because no username, password, host, port and to provided.");
		}
		
		this.properties = new Properties();
		properties.put("mail.smtp.auth", this.auth.equals("true")==true?true:false);
		properties.put("mail.smtp.starttls.enable", this.starttls);
		properties.put("mail.smtp.ssl.enable", this.ssl);
		properties.put("mail.smtp.host", this.host);
		properties.put("mail.smtp.port", this.port);
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setPoolExecutor(ExecutorService poolExecutor) {
		this.poolExecutor = poolExecutor;
	}

	@Override
	public void start() throws GGEventsConnectorException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() throws GGEventsConnectorException {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerConsumer(IGGEventsContextSubscription subscription, IGGEventsMessageHandler messageHandler, String tenantId, String clusterId, String assetId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerProducer(IGGEventsContextSubscription subscription, String tenantId, String clusterId, String assetId) {
		this.mailSenders .put(subscription.getTopic(), new GGEventsMailSender(this.properties, this.from, this.to, this.object, this.body, this.userName, this.password, this.contentType));
	}

}
