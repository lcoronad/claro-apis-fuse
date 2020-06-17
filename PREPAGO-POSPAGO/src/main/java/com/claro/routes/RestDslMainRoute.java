package com.claro.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.claro.dto.Request;
import com.claro.dto.RequestAplicarCargo;
import com.claro.dto.RequestAprovisionamiento;
import com.claro.dto.RequestCatalogo;
import com.claro.dto.Response;
import com.claro.dto.ResponseAprovisionamiento;
import com.claro.utils.ConstantUtil;

import io.swagger.annotations.Api;

/**
 * 
 * @author Assert Solutions S.A.S
 *
 */
@Component
@Api(value = "Initial Proyect Camel-REST", description = "Estrucutura Basica Proyecto Rest Y Camel")
public class RestDslMainRoute extends RouteBuilder {

    @Value("${camel.component.servlet.mapping.context-path}")
    private String contextPath;

    @Autowired
    private Environment env;
    private Logger log = LoggerFactory.getLogger(RestDslMainRoute.class);

    @Override
    public void configure() throws Exception {
    // @formatter:off
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true")
            .enableCORS(true)
            .port(env.getProperty("server.port", "8080"))
            .contextPath(contextPath.substring(0, contextPath.length() - 2))
            // turn on swagger api-doc
            .apiContextPath("/api-doc")
            .apiProperty("api.title",  env.getProperty("api.title"))
            .apiProperty("api.version", env.getProperty("api.version"));
        
        rest().description(env.getProperty("api.description"))
            .consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
            .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
       
       .post(env.getProperty("endpoint.api.consultar.factura")).description(env.getProperty("api.description.service.paradigma")).type(Request.class).description(
                 env.getProperty("api.description.input.post")).outType(Response.class) 
             .responseMessage().code(200).message("Consulta Facturación ok").endResponseMessage()
             .to(ConstantUtil.ROUTE_PARADIGMA)
       .post(env.getProperty("endpoint.api.consuta.catalogo")).description(env.getProperty("api.description.service.itel.catalogo")).type(RequestCatalogo.class)
       			.outType(String.class)
       			.responseMessage().code(200).message("Catálogo ok").endResponseMessage()
       			.to(ConstantUtil.ROUTE_LOAD_CATALOGO)		
       	.post(env.getProperty("endpoint.api.adquirir.paquete")).description(env.getProperty("api.description.service.itel")).type(RequestAprovisionamiento.class)
       			.outType(ResponseAprovisionamiento.class)
       			.responseMessage().code(200).message("Aprovisionamiento ok").endResponseMessage()
       			.to(ConstantUtil.ROUTE_APROVISIONAMIENTO)
		.post(env.getProperty("endpoint.api.pagar.factura")).description(env.getProperty("api.description.service.motorpagos")).type(RequestAplicarCargo.class)
       			.outType(RequestAplicarCargo.class)
       			.responseMessage().code(200).message("Pago ok").endResponseMessage()
       			.to(ConstantUtil.ROUTE_APLICAR_CARGO)
       	.get(env.getProperty("endpoint.api.get")).description(env.getProperty("endpoint.api.get.description"))
       		.type(String.class).outType(String.class).responseMessage().code(200).endResponseMessage().to("direct:get");

		onException(Exception.class).handled(true)
				.log(LoggingLevel.ERROR, log,
						"Proceso: ${exchangeId} | Mensaje: Error en la entrada de datos. | Causa: ${exception.message}")
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400)).setBody(constant("{'error':'Entrada invalida'}"))
				.end();
		
		from("direct:get")
     		.setBody(constant("OK"))
     	.end();
		// @formatter:on
	}

}
