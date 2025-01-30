package com.garganttua.events.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.garganttua.events.spec.annotations.GGEventsProcessor;
import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.exceptions.GGEventsHandlingException;
import com.garganttua.events.spec.interfaces.IGGEventsEngine;
import com.garganttua.events.spec.interfaces.IGGEventsObjectRegistryHub;
import com.garganttua.events.spec.interfaces.IGGEventsProcessor;
import com.garganttua.events.spec.objects.GGEventsConfigurationDecoder;
import com.garganttua.events.spec.objects.GGEventsContextObjDescriptor;
import com.garganttua.events.spec.objects.GGEventsExchange;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@GGEventsProcessor(type = "filter", version = "1.0")
public class GGEventsFilterProcessor implements IGGEventsProcessor {

	private String type = "processor::filter";
	private String configuration;
	private String tenantId;
	private String clusterId;
	private String assetId;
	private IGGEventsObjectRegistryHub objectRegistries;
	private IGGEventsEngine engine;
	private String name;
	private String infos;
	private String manual;
	private ExecutorService service;
	private ScheduledExecutorService scheduledService;
	private String filter;
	private List<String> variables;

	@Override
	public boolean handle(GGEventsExchange exchange) throws GGEventsHandlingException {
		
		String expression = new String(this.filter);
		
		for( String variable: this.variables) {
			String value;
			try {
				value = GGEventsExchange.getVariableValue(exchange, variable);
			} catch (GGEventsException e) {
				throw new GGEventsHandlingException(e);
			}
			value = formatString(value);
			expression = expression.replace(variable, value);
		}
		
		try {
			ExpressionParser parser = new SpelExpressionParser();
			Expression exp = parser.parseExpression(expression); 
			boolean result = (boolean) exp.getValue();
			
			if( !result ) {
				log.info("[TenantId:"+exchange.getTenantId()+"][ClusterId:"+this.clusterId+"][ExchangeId:"+exchange.getExchangeId()+"][CorrelationId:"+exchange.getCorrelationId()+"][MessageId:"+exchange.getMessageId()+"] Exchange does not match filter expression "+this.filter);
				return false;
			}
		} catch(Exception e) {
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
	}

	@Override
	public void applyConfiguration() throws GGEventsException {
		Map<String, List<String>> __configuration__ = GGEventsConfigurationDecoder
				.getConfigurationFromString(this.configuration);

		for (Entry<String, List<String>> entry : __configuration__.entrySet()) {
			switch (entry.getKey()) {
			case "filter":
				this.filter = entry.getValue().get(0);
				break;
			}
		}
		variables = GGEventsFilterProcessor.findvariables(this.filter);
	}

	protected static List<String> findvariables(String chaine) {
		List<String> sousChaines = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
		Matcher matcher = pattern.matcher(chaine);

		while (matcher.find()) {
			String var = matcher.group(1);
			sousChaines.add("${"+var+"}");
		}

		return sousChaines;
	}

	@Override
	public GGEventsContextObjDescriptor getDescriptor() {
		return new GGEventsContextObjDescriptor(this.getClass().getCanonicalName(), "filter", "1.0", this.infos,
				this.manual);
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
	public void setExecutorService(ExecutorService service) {
		this.service = service;
	}

	@Override
	public void setScheduledExecutorService(ScheduledExecutorService service) {
		this.scheduledService = service;
	}

	@Override
	public String getType() {
		return this.type;
	}
	
    public static String formatString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "''"; 
        }

      
        if (isNumeric(input)) {
            return input; 
        }

      
        return "'" + input + "'";
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
           
        }

        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
           
        }

        return false;
    }

}
