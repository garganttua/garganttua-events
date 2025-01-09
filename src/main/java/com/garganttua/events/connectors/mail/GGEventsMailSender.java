package com.garganttua.events.connectors.mail;

import java.util.Properties;

import com.garganttua.events.spec.exceptions.GGEventsException;
import com.garganttua.events.spec.objects.GGEventsExchange;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class GGEventsMailSender {

	private Properties properties;
	private String from;
	private String body;
	private String username;
	private String password;
	private String to;
	private String object;
	private String contentType;

	public GGEventsMailSender(Properties properties, String from, String to, String object, String body, String username, String password, String contentType) {
		this.properties = properties;
		this.from = from;
		this.to = to;
		this.body = body;
		this.object = object;
		this.username = username;
		this.password = password;
		this.contentType = contentType;
	}

	public void sendMail(GGEventsExchange exchange) throws AddressException, MessagingException, GGEventsException {

        Session session = Session.getInstance(this.properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(this.from==null?this.username:this.from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(getRecipient(exchange)));
		message.setSubject(this.getObject(exchange));
		message.setContent(this.getBody(exchange), this.getContentType(exchange));
		message.setHeader("dataflowUuid", exchange.getToDataflowUuid());
		Transport.send(message);
	}

	private String getObject(GGEventsExchange exchange) throws GGEventsException {
		String subject = null;
		
		if( this.object == null ) {
			subject = exchange.getToDataflowUuid()+exchange.getToTopic();
		} else {
			if( GGEventsExchange.isVariable(this.object) ) {
				subject = GGEventsExchange.getVariableValue(exchange, this.object);
			} else {
				subject = this.object;
			}
		}
		return subject;
	}

	private String getContentType(GGEventsExchange exchange) {
		String contentType = null;
		if( this.contentType == null ) {
			return exchange.getContentType();
		} else {
			try {
				contentType = GGEventsExchange.getVariableValue(exchange, this.contentType);
			} catch (GGEventsException e) {
				contentType = this.contentType;
			}
		}
		return contentType;
	}

	private String getBody(GGEventsExchange exchange) {
		String body = null;
		
		if( this.body == null ) {
			body = new String(exchange.getValue());
		} else {
			try {
				if( GGEventsExchange.isVariable(this.body) ) {
					body = GGEventsExchange.getVariableValue(exchange, this.body);
				} else {
					body = this.body;
				}
			} catch (GGEventsException e) {
				body = this.body;
			}
		}
		return body;
	}

	private String getRecipient(GGEventsExchange exchange) {
		String to = null;
		
		try {
			if( GGEventsExchange.isVariable(this.to) ) {
				to = GGEventsExchange.getVariableValue(exchange, this.to);
			} else {
				to = this.to;
			}
		} catch (GGEventsException e) {
			to = this.to;
		}
		
		return to;
	}

}
