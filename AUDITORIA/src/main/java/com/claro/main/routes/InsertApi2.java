package com.claro.main.routes;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.claro.dto.AuditApi2;
import com.claro.processor.BuildHeadersApi2;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class InsertApi2 extends RouteBuilder {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String ROUTE_API_2= "direct:insert-api-2";

	@Override
	public void configure() throws Exception {
		
		from(ROUTE_API_2).id("RutaAutenticacion").streamCaching("true")
		 .onException(Exception.class)
		 	.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} || mensaje: ${exception.message}")
		 	.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} || mensaje: ${body}")
		 .end()
		 .doTry()
		 	.process(exchange ->{
		 		String body = exchange.getIn().getBody(String.class);
		 		AuditApi2 audit = new AuditApi2();
		 		ObjectMapper objectMapper = new ObjectMapper();
		 		logger.debug("Proceso: {} || Mensaje antes de procesar: {}",exchange.getProperty("procesoId"),body);
		 		
		 		audit = objectMapper.readValue(body.getBytes(StandardCharsets.UTF_8), AuditApi2.class);
		 		exchange.getIn().setBody(audit);
		 	})
		 .endDoTry()
		 .doCatch(Exception.class)
		 	.log(LoggingLevel.ERROR, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Error al mapear el mensaje para insertar | Detalle: ${exception.message}")
		 	.stop()
		 .end()
		 .marshal().json(JsonLibrary.Jackson)
		 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Mensaje serializado  | ${body}")
		 .unmarshal().json(JsonLibrary.Jackson, AuditApi2.class)
		 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a construir los headers para el insert")	
		 .process(new BuildHeadersApi2())
		 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a ejecutar el SP ({{sp.insert.log}})")
		 .doTry()
				.to("sql-stored:{{sp.insert.api.auth}}(VARCHAR ${header.idCliente},VARCHAR ${header.request},VARCHAR ${header.codigoRespuesta},"
						+ "VARCHAR ${header.detalleR},VARCHAR ${header.response},VARCHAR ${header.api},"
						+ "VARCHAR ${header.recursoApp},VARCHAR ${header.nombreCliente},VARCHAR ${header.logUsuario},"
						+ "VARCHAR ${header.logUUIDTxr},TIMESTAMP ${header.fechaTransaccion},"
						+ "VARCHAR ${header.usuario},VARCHAR ${header.min_code},VARCHAR ${header.tipoDoc},VARCHAR ${header.numeroDoc},"
						+ "VARCHAR ${header.cuenta})?dataSource=dataSource")
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} || mensaje: Termino de ejecutar ({{sp.insert.log}})")
		 .endDoTry()
		 .doCatch(SQLException.class)
				.log(LoggingLevel.ERROR, logger, "Proceso:  ${exchangeProperty.procesoId} | Error: ${exception.message}")
		 .end()
		.end();
		
		
	}

}
