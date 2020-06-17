package com.claro.jms;

import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Handler;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.claro.main.routes.MainRoute;

@Component
public class JmsConsumer {

	private Logger logger = LoggerFactory.getLogger(JmsConsumer.class);
	
	@EndpointInject(uri = MainRoute.ROUTE_CONSUMER)
	private ProducerTemplate producerT;
	
	@Handler
	@JmsListener(destination = "${spring.jms.template.default-destination}")
	public void reciveMessage(String message, @Headers Map<String, Object> mapHeaders) {
		logger.info("Mensaje recibido:{}", message);
		
		try {
			producerT.sendBodyAndHeaders(message, mapHeaders);
						
		} catch (Exception e) {
			logger.error("Error:{}" ,e.getMessage());
		}

	}
}
