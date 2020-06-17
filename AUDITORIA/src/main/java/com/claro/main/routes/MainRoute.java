package com.claro.main.routes;


import java.nio.charset.StandardCharsets;
import java.sql.SQLException;


import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestHostNameResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.claro.dto.Audit;
import com.claro.jms.JmsConsumer;
import com.claro.processor.BuildHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;



@Component
public class MainRoute extends RouteBuilder {

	@Autowired
	private  Environment env;
	@Value("${camel.component.servlet.mapping.context-path}")
	private String servlet;
	public static final  String ROUTE_CONSUMER= "direct://api-consumer";
	public static final String ROUTE_SP_API1 = "direct://api-sp-1";
	public static final String ROUTE_START ="direct://api-start";
	
	
	private Logger logger = LoggerFactory.getLogger(MainRoute.class);
	
	@Override
	public void configure() throws Exception {
		
		restConfiguration()
			.component("servlet")
			.bindingMode(RestBindingMode.json)
			.dataFormatProperty("prettyPrint", "true")
			.enableCORS(true)
			.port(env.getProperty("server.port"))
			.hostNameResolver(RestHostNameResolver.localIp)
			.contextPath(servlet.substring(0, servlet.length() - 2))
			 // turn on swagger api-doc
            .apiContextPath("/api-doc")
            .apiProperty("api.title",  env.getProperty("api.title"))
            .apiProperty("api.version", env.getProperty("api.version"));
		
		rest().description(env.getProperty("api.description"))
			.consumes("application/json")
			.produces("application/json")
			.get(env.getProperty("endpoint.api")).type(String.class).outType(String.class)
			.to(ROUTE_START)
			.get("/healthcheck").type(String.class).outType(String.class).to("direct:healthcheck");
		
		from("direct:healthcheck").routeId("Healthcheck")
			.streamCaching("true")
			.onException(Exception.class)
				.log(LoggingLevel.ERROR, logger, "Proceso:  ${exchangeProperty.procesoId} | |  Error: ${exception.message} ")
			.end()
//			.log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeId} || mensaje: Inicio consumidor de mensajes")
			.setBody(constant("OK"))
			.end();
		
		from(ROUTE_START).routeId("ExponeServie")
			.streamCaching("true")
			.onException(Exception.class)
				.log(LoggingLevel.ERROR, logger, "Proceso:  ${exchangeProperty.procesoId} | |  Error: ${exception.message} ")
			.end()
			.setProperty("procesoId", simple("${exchangeId}"))
			.log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio consumidor de mensajes")
			.bean(JmsConsumer.class)
			.setBody(constant("OK"))
			.end();
		
		from(ROUTE_CONSUMER).routeId("ConsumidorBroker").streamCaching("true")
			.onException(Exception.class)
				.log(LoggingLevel.ERROR, logger, "Error: ${exception.message}")
			.end()
			.setProperty("procesoId", simple("${exchangeId}"))
			.log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a consumir mensaje")
			.log(LoggingLevel.DEBUG, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Cuerpo del mensaje = ${body}")
			.choice()
				.when(simple("${body} == null || ${body} == ''"))
					.log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: No hay mensajes por leer ${body}")
				.endChoice()
				.otherwise()
				 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a leer el mensaje || Headers recividos: ${headers}")	
				
				 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a registrar auditoria para: ${headers.api_name}")
				 .setHeader("api1", simple("{{auditoria.app.geolocalizacion}}"))
				 .setHeader("api2", simple("{{auditoria.app.login}}"))
				 .setHeader("api3", simple("{{auditoria.app.factura}}"))
				 .setHeader("apiPrepagoPospago", simple("{{auditoria.app.prepago.pospago}}"))
				 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Headers totales: ${headers}")
				 .choice()
				 	.when(simple("${headers.api_name} == ${headers.api1} "))
				 		.to(ROUTE_SP_API1)
				 	.endChoice()
				 	.when(simple("${headers.api_name} == ${headers.api2} "))
			 			.to(InsertApi2.ROUTE_API_2)
			 		.endChoice()
			 		.when(simple("${headers.api_name} == ${headers.api3} "))
			 			
			 		.endChoice()
			 		.when(simple("${headers.api_name} == ${headers.apiPrepagoPospago} "))
			 			.to(InsertApiPrepagoPospago.ROUTE_API_RAPPI)
			 		.endChoice()
			 		.otherwise()
			 			.log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: No hay coincidencias || Id: ${headers.api_name}")
			 		.endChoice()
				 .end()
			
				 
				 .endChoice()
			.end()
			
			.log(LoggingLevel.INFO, logger, "Finalizo :${body}")
		.end();
		
		from(ROUTE_SP_API1).routeId("RouteSpApi1").streamCaching("true")
		 .doTry()
		 	.process(exchange ->{
		 		String body = exchange.getIn().getBody(String.class);
		 		Audit audit = new Audit();
		 		ObjectMapper objectMapper = new ObjectMapper();
		 		logger.debug("Proceso: {} || Mensaje antes de procesar: {}",exchange.getProperty("procesoId"),body);
		 		
		 		audit = objectMapper.readValue(body.getBytes(StandardCharsets.UTF_8), Audit.class);
		 		exchange.getIn().setBody(audit);
		 	})
		 .endDoTry()
		 .doCatch(Exception.class)
		 	.log(LoggingLevel.ERROR, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Error al mapear el mensaje para insertar | Detalle: ${exception.message}")
		 	.stop()
		 .end()
		 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a construir los headers para el insert")	
		 .process(new BuildHeaders())
		 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a ejecutar el SP ({{sp.insert.log}})")
		 .doTry()
				.to("sql-stored:{{sp.insert.log}}(VARCHAR ${header.idCliente},VARCHAR ${header.request},TIMESTAMP ${header.fechaTransaccion},"
						+ "VARCHAR ${header.response} , VARCHAR ${header.api}, VARCHAR ${header.codigoRespuesta},VARCHAR ${header.detalleR}, "
						+ "VARCHAR ${header.recursoApp},VARCHAR ${header.cliente} ,VARCHAR ${header.logUsuario},"
						+ "VARCHAR ${header.logUUIDTxr},VARCHAR ${header.min_code},"
						+ "VARCHAR ${header.dpto},VARCHAR ${header.ciudad},VARCHAR ${header.pais},VARCHAR ${header.fechaU},"
						+ "VARCHAR ${header.latitudLbs}, VARCHAR ${header.longitudLbs}, VARCHAR ${header.statusLBS},"
						+ "VARCHAR ${header.requestLBS}, VARCHAR ${header.responseLBS}, VARCHAR ${header.statusPIC},"
						+ "VARCHAR ${header.responsePIC})?dataSource=dataSource")
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} || mensaje: Termino de ejecutar ({{sp.insert.log}})")
		 .endDoTry()
		 .doCatch(SQLException.class)
				.log(LoggingLevel.ERROR, logger, "Proceso:  ${exchangeProperty.procesoId} | Error: ${exception.message}")
		 .end()
		 .end();
		
	}

}
