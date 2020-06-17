package com.claro.routes;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.claro.beans.ResponseHandler;
import com.claro.configuration.MyException;
import com.claro.dto.RequestCatalogo;
import com.claro.process.ConfigAuditoriaProcessor;
import com.claro.process.ErrorProcessor;
import com.claro.utils.ConstantUtil;

@Component
public class RouteLoadCatalogo extends RouteBuilder {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${canal.catalogo}")
	private String[] elementToSearch;

	@Override
	public void configure() throws Exception {

		from(ConstantUtil.ROUTE_LOAD_CATALOGO).routeId("ROUTE_LOAD_CATALOGO").streamCaching("true")
				.setProperty(ConstantUtil.API_NAME, simple("{{api.name}}"))
				.setProperty(ConstantUtil.LOG_USUARIO, simple("{{api.log.name}}"))
				.setProperty(ConstantUtil.TIPO_PRODUCTO, simple("{{api.tipo.producto.prepago}}"))
				.onException(BeanValidationException.class).handled(true)
				.log(LoggingLevel.ERROR, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en la validación de los campos | Causa: ${exception.message}")
				.setProperty(ConstantUtil.EXCEPTION, simple("${exception.message}")).process(new ErrorProcessor())
				.log(LoggingLevel.ERROR, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Se encontro una excepción en el mensaje de entrada | Causa:${exchangeProperty.listErrorDetails}")
				.bean(ResponseHandler.class,
						"errorValidatorBeans({{message.codigo.failed}},message.error.validator,${exchangeProperty.listErrorDetails})")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
				.setProperty(ConstantUtil.COD_RESPONSE, simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.DETALE_RESPONSE, simple("{{message.error.validator}}"))
				.setProperty(ConstantUtil.RESPONSE).body().inOnly(ConstantUtil.ROUTE_AUDITORIA).end()
				.onException(Exception.class).handled(true)
				.log(LoggingLevel.ERROR, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en ejecución | Causa: ${exception.message}")
				.bean(ResponseHandler.class,
						"responseCatalogo({{message.codigo.failed}},message.detalle.catalogo.failed,[])")
				.setProperty(ConstantUtil.COD_RESPONSE, simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.DETALE_RESPONSE, simple("{{message.detalle.catalogo.failed}}"))
				.setProperty(ConstantUtil.RESPONSE).body().inOnly(ConstantUtil.ROUTE_AUDITORIA).end()
				.onException(MyException.class)
					.handled(true)
					.log(LoggingLevel.ERROR, logger,"Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en ejecución | Causa: ${exception.message}")
					.bean(ResponseHandler.class, "responseCatalogo({{message.codigo.failed}},${exception.message},[])")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
					.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
					.setProperty(ConstantUtil.COD_RESPONSE, simple("{{message.codigo.failed}}"))
					.setProperty(ConstantUtil.DETALE_RESPONSE, simple("{{message.detalle.catalogo.failed.isprepago}}"))
					.setProperty(ConstantUtil.RESPONSE).body()
					.inOnly(ConstantUtil.ROUTE_AUDITORIA)
				.end()
				.setHeader(ConstantUtil.ENDPOINT, simple("{{endpoint.api.consuta.catalogo}}"))
				
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje:  Nombre app ${headers.api-name}")
				.marshal().json(JsonLibrary.Jackson)
				.process(new ConfigAuditoriaProcessor())
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio la ruta")
				.setProperty(ConstantUtil.CANAL).jsonpath("$.*.canal")
				.setProperty(ConstantUtil.MIN_APROVISIONAMIENTO).jsonpath("$.*.min")
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Valor canal ${exchangeProperty.canal}")
				.choice()
					.when(simple("${exchangeProperty.canal} ==  '1' || ${exchangeProperty.canal} ==  '2' || ${exchangeProperty.canal} ==  '3' "))
						.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Funciona")
						.process(e -> e.setProperty(ConstantUtil.NOMBRE_CANAL,
								this.elementToSearch[Integer.valueOf(e.getProperty("canal", String.class)) - 1]))
						.end()
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: ${body}").unmarshal()
				.json(JsonLibrary.Jackson, RequestCatalogo.class).setProperty(ConstantUtil.NOMBRE_SE, constant("ITEL"))
				.to("bean-validator:validator")
				.removeHeader("*")
				.setHeader(ConstantUtil.MIN_APROVISIONAMIENTO, simple("${exchangeProperty.min}", Long.class))
				.setHeader("test", constant(false))
				.log(LoggingLevel.INFO, logger,	"Proceso: ${exchangeProperty.procesoId} | Mensaje: Número a consultar | ${headers.min}")
				.process(e -> e.setProperty(ConstantUtil.NOMBRE_CANAL,this.elementToSearch[Integer.valueOf(e.getProperty("canal", String.class)) - 1]))
				.to("sql:{{bscs.catalogo.isprepago}}?dataSource=dataSourceBSCS")
				.convertBodyTo(String.class)
				.log(LoggingLevel.INFO, logger,"Proceso: ${exchangeProperty.procesoId} | Mensaje: Resultado del select | ${body}")
				
				.choice()
					.when().spel("#{request.body.contains('0')}")
						.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: {{message.detalle.catalogo.failed.isprepago}}")
						.throwException(MyException.class, "message.detalle.catalogo.failed.isprepago")
					.otherwise()
						.process(e->{
							Map<String, Object> body= new HashMap<>();
							body.put("min", e.getProperty(ConstantUtil.MIN_APROVISIONAMIENTO,Long.class));
							e.getIn().setBody(body);
						})
						.log(LoggingLevel.INFO, logger,	"Proceso: ${exchangeProperty.procesoId} | Mensaje: Número a consultar | ${body}")
						.to("sql-stored:{{bscs.validate.isactivo}}?dataSource=dataSourceBSCS")
						.log(LoggingLevel.INFO, logger,"Proceso: ${exchangeProperty.procesoId} | Mensaje: Resultado del sp | ${body}")
						.process(e->{
							@SuppressWarnings("unchecked")
							Map<String, Object> body= e.getIn().getBody(Map.class);
							if(!body.get("S_vcs_stat_chng").equals("Activo")) {
								throw new MyException("message.detalle.catalogo.failed.isprepago");
							}
						})	
					.end()
				.to(ConstantUtil.ROUTE_LOAD_CACHE)
				.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje:Canal ${body}")
				.setProperty(ConstantUtil.COD_RESPONSE, simple("{{message.codigo.ok}}"))
				.setProperty(ConstantUtil.DETALE_RESPONSE, simple("{{message.detalle.catalogo.ok}}"))
				.setProperty(ConstantUtil.RESPONSE).body()
				.inOnly(ConstantUtil.ROUTE_AUDITORIA)
				.end();
				
				// @formatter:off
	}

}
