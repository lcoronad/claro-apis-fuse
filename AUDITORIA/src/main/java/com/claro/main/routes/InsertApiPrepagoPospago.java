package com.claro.main.routes;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.claro.dto.AuditRappi;
import com.claro.processor.BuildHeadersApiRappi;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class InsertApiPrepagoPospago extends RouteBuilder {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String ROUTE_API_RAPPI= "direct:insert-prepago-pospago";

	@Override
	public void configure() throws Exception {
		
		from(ROUTE_API_RAPPI).id("RutaRappi").streamCaching("true")
		 .onException(Exception.class)
		 	.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} || mensaje: ${exception.message}")
		 	.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} || mensaje: ${body}")
		 .end()
		 .doTry()
		 	.process(exchange ->{
		 		String body = exchange.getIn().getBody(String.class);
		 		AuditRappi audit = new AuditRappi();
		 		ObjectMapper objectMapper = new ObjectMapper();
		 		logger.debug("Proceso: {} || Mensaje antes de procesar: {}",exchange.getProperty("procesoId"),body);
		 		audit = objectMapper.readValue(new String(body.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8), AuditRappi.class);
		 		exchange.getIn().setBody(audit);
		 	})
		 .endDoTry()
		 .doCatch(Exception.class)
		 	.log(LoggingLevel.ERROR, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Error al mapear el mensaje para insertar | Detalle: ${exception.message}")
		 	.stop()
		 .end()
		 .marshal().json(JsonLibrary.Jackson)
		 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Mensaje serializado  | ${body}")
		 .unmarshal().json(JsonLibrary.Jackson, AuditRappi.class)
		 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a construir los headers para el insert")	
		 .process(new BuildHeadersApiRappi())
		 .log(LoggingLevel.INFO, logger, "Proceso:  ${exchangeProperty.procesoId} || mensaje: Inicio a ejecutar el SP ({{sp.insert.api.prepago.pospago}})")
		 .log(LoggingLevel.DEBUG, logger, "Proceso:  ${exchangeProperty.procesoId} || Todos los heades \n ${headers}")
		 .doTry()
				.to("sql-stored:{{sp.insert.api.prepago.pospago}}(VARCHAR ${headers.idCliente}, VARCHAR ${headers.httpRequest},"
						+ "VARCHAR ${headers.codRespuesta}, VARCHAR ${headers.detalleRespuesta}, VARCHAR ${headers.HttpResponse},"
						+ "VARCHAR ${headers.api}, VARCHAR ${headers.recurso}, VARCHAR ${headers.nombreCliente},"
						+ "VARCHAR ${headers.logUsuario}, VARCHAR ${headers.logUUIDTxr},"
						+ "TIMESTAMP ${headers.fechaTransaccion}, VARCHAR ${headers.canalContacto},"
						+ "VARCHAR ${headers.canalContactoNombre}, VARCHAR ${headers.codigoMin},"
						+ "VARCHAR ${headers.transaccionEstado}, VARCHAR ${headers.paqueteTipoProducto},"
						+ "VARCHAR ${headers.paqueteNombreProducto}, VARCHAR ${headers.codigoRespuesta},"
						+ "VARCHAR ${headers.mensajeRespuesta}, VARCHAR ${headers.requestServicio},"
						+ "VARCHAR ${headers.nombreServicio}, VARCHAR ${headers.tipoProducto})?dataSource=dataSource")
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} || mensaje: Termino de ejecutar ({{sp.insert.api.prepago.pospago}})")
		 .endDoTry()
		 .doCatch(SQLException.class)
				.log(LoggingLevel.ERROR, logger, "Proceso:  ${exchangeProperty.procesoId} | Error: ${exception.message}")
		 .end()
		.end();
	}
}
