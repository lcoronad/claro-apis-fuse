package com.claro.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.claro.configuration.JmsConfiguration;
import com.claro.process.BuildAuditoriaProcessor;
import com.claro.utils.ConstantUtil;

@Component
public class RouteAuditoria extends RouteBuilder {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void configure() throws Exception {
		
		from(ConstantUtil.ROUTE_AUDITORIA).routeId("ROUTE_AUDITORIA").streamCaching("true")
			.onException(Exception.class)
				.handled(true)
				.log(LoggingLevel.ERROR,logger ,"Proceso: ${exchangeProperty.procesoId} | Mensaje: Error enviando mensaje el broker | Causa: ${exception.message}")
			.end()
			.process(new BuildAuditoriaProcessor())
			.setBody(simple("${exchangeProperty.auditoria}"))
			.marshal().json(JsonLibrary.Jackson)
			.bean(JmsConfiguration.class)
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Enviando mensaje al broker | Detalle: ${body}")
		.end();
		
	}

}
