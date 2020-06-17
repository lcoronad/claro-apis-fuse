package com.claro.configuration;

import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableJms
public class JmsConfiguration {
	
	Logger logger = LoggerFactory.getLogger(JmsConfiguration.class);
	
	private static final String API_NAME = "api_name";

	@Autowired
	private JmsTemplate jmsTemplate;
	

	@Autowired
	private Environment env;
	
	@Handler
	public void sendMessage(String payload) {
		this.jmsTemplate.convertAndSend(env.getProperty("spring.activemq.queue"), payload, m ->{
			m.setStringProperty(API_NAME, env.getProperty("api.name"));
			return m;
		});

	}

}
