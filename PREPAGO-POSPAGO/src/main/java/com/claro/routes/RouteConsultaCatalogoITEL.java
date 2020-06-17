package com.claro.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.claro.beans.ResponseHandler;
import com.claro.utils.ConstantUtil;

@Component
public class RouteConsultaCatalogoITEL  extends RouteBuilder{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void configure() throws Exception {
		
		from(ConstantUtil.ROUTE_CONSULTA_CATALOGO_ITEL).routeId("ROUTE_CONSULTA_CATALOGO_ITEL").streamCaching()
			.errorHandler(noErrorHandler())
			.log(LoggingLevel.INFO,logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio a consultar catalogo ITEL")
			.setProperty("usuario").simple("{{itel.catalogo.usuario}}")
			.setProperty("password").simple("{{itel.catalogo.password}}")
			.setProperty("applicationid").simple("{{itel.catalogo.applicationid}}")
			.setProperty("transaccionid").simple("{{itel.catalogo.transaccionid}}")
			.setProperty("grupo").simple("{{itel.catalogo.busqueda}}")
			.setProperty("name").simple("{{itel.catalogo.busqueda.name}}")
			.setProperty("valor").simple("{{itel.catalogo.busqueda.valor}}")
			.to("velocity:loadcatalogo.vm")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Plantilla cargada | Detalle: \n ${body}")
			.setProperty(ConstantUtil.REQUEST_SE).body(String.class)
			.setHeader(Exchange.HTTP_URI, simple("{{itel.catalogo.url}}"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.to("http4:dummy?throwExceptionOnFailure=true")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: El valor de test= ${headers.test}")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Respuesta del servicio = ${body}")
//			.choice()
//				.when(header("test").isEqualTo("true"))
//					.to("velocity:responsecatalogo.vm")
//				
//				.when(header("test").isEqualTo("false"))
//					.to("velocity:responsecatalogo2.vm")
//				
//				.end()
			
				
			
//			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Respuesta del servicio | Detalle: \n ${body}")
			.setProperty(ConstantUtil.RESPONSE_SE).body(String.class)
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
			.setProperty(ConstantUtil.RESPNSE_CODE_SE,simple("${headers.CamelHttpResponseCode}"))
			.setHeader("status").jsonpath("$.*.resultCode.resultCode")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Estado de respuesta | Detalle: \n ${headers.status}")
			.choice()
				.when(simple("${headers.status} != 0"))
					.throwException(Exception.class, "Error del servicio con status: ${headers.status}")
				
					.end()
			
			.setBody().jsonpath("$.*.productList.products")
			.bean(ResponseHandler.class,"responseCatalogo({{message.codigo.ok}},message.detalle.catalogo.ok,${body})")
			.end();
		
		
	}
	
}
