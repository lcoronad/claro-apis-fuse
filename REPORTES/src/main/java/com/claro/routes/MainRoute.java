package com.claro.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestHostNameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


@Component
public class MainRoute extends RouteBuilder{
	
	@Autowired
	private Environment env;
	
	private Logger logger = LoggerFactory.getLogger(MainRoute.class);
	
	public static final String MAIN_ROUTE = "direct://reporte-auditoria";
	public static final String PROCESO_ID = "procesoId";

	@Override
	public void configure() throws Exception {
		
		restConfiguration()
			.component("servlet")
			.bindingMode(RestBindingMode.json)
			.dataFormatProperty("prettyPrint", "true")
			.enableCORS(true)
			.port(env.getProperty("server.port"))
			.hostNameResolver(RestHostNameResolver.localIp)
			.apiContextPath("/api-doc")
			.apiProperty("api.title", "Rest Api Reportes")
			.apiProperty("api.version", "v1")
			.apiContextRouteId("doc-api");
		
		rest(env.getProperty("reporte.endpoint"))
			.consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
			.produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
			.get().type(String.class).outType(String.class).to(MAIN_ROUTE);
		
		rest("/healthcheck")
		.consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
		.produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
		.get().type(String.class).outType(String.class).to("direct:healthcheck");
		
		from(MAIN_ROUTE).routeId("Reporte").streamCaching()
			.onException(Exception.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso= ${exchangeProperty.procesoId} | mensaje= Error : ${exception.message} ")
				.setBody(simple("Error: ${exception.message}"))
			.end()
				.setProperty(PROCESO_ID, simple("${exchangeId}"))
				.log(LoggingLevel.INFO, logger, "Proceso= ${exchangeProperty.procesoId} | mensaje= Inicio a procesar el endpoint")
				.to(GenerarReporte.GENERA_REPORTE_ROUTE)
				.setBody(simple("OK"))
			.end();
		
		from("direct:healthcheck").routeId("HEALTH_CHECK")
//			.log(LoggingLevel.INFO, logger, "Proceso= ${exchangeId} | mensaje= Inicio a procesar el endpoint health")
			.setBody(constant("OK"))
			.end();
		
		
	}

}
