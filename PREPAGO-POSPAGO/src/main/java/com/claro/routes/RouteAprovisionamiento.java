package com.claro.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.claro.beans.ResponseHandler;
import com.claro.dto.RequestAprovisionamiento;
import com.claro.process.ConfigAuditoriaProcessor;
import com.claro.process.ErrorProcessor;
import com.claro.utils.ConstantUtil;

@Component
public class RouteAprovisionamiento  extends RouteBuilder{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void configure() throws Exception {
		
		from(ConstantUtil.ROUTE_APROVISIONAMIENTO).routeId("ROUTE_APROVISIONAMIENTO").streamCaching("true")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio de operacion de aprovisionamiento")
			.setProperty(ConstantUtil.API_NAME, simple("{{api.name}}"))
			.setProperty(ConstantUtil.LOG_USUARIO, simple("{{api.log.name}}"))
			.setProperty(ConstantUtil.CANAL).jsonpath("$.data.canal")
			.setProperty(ConstantUtil.NOMBRE_SE, simple("{{itel.aprovisionamiento.nombre}}"))
			.setProperty(ConstantUtil.TIPO_PRODUCTO, simple("{{api.tipo.producto.prepago}}"))
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Obteniendo el nombre del canal")
			.choice()
				.when().simple("${exchangeProperty.canal} == 1")
					.setProperty(ConstantUtil.NOMBRE_CANAL, simple("{{canal.uno}}"))
				.endChoice()
				.when().simple("${exchangeProperty.canal} == 2")
					.setProperty(ConstantUtil.NOMBRE_CANAL, simple("{{canal.dos}}"))
				.endChoice()
				.when().simple("${exchangeProperty.canal} == 3")
					.setProperty(ConstantUtil.NOMBRE_CANAL, simple("{{canal.tres}}"))
				.endChoice()
				.otherwise()
				.setProperty(ConstantUtil.NOMBRE_CANAL, constant(""))
					.log("Error: Canal invalido")
				.endChoice()
			.end()
			.setProperty(ConstantUtil.MIN_APROVISIONAMIENTO).jsonpath("$.data.min")
			.setProperty(ConstantUtil.OFFERID_APROVISIONAMIENTO).jsonpath("$.data.offerId")
			.setProperty(ConstantUtil.OFFERID_NOMBRE).jsonpath("$.data.nombrePaquete")
			.onException(BeanValidationException.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en la validación de los campos | Causa: ${exception.message}")
				.setProperty(ConstantUtil.EXCEPTION, simple("${exception.message}"))
				.process(new ErrorProcessor())
				.log(LoggingLevel.ERROR, logger,"Proceso: ${exchangeProperty.procesoId} | Mensaje: Se encontro una excepción en el mensaje de entrada | Causa:${exchangeProperty.listErrorDetails}")
				.bean(ResponseHandler.class,"errorValidatorBeans({{message.codigo.failed}},message.error.validator,${exchangeProperty.listErrorDetails})")
				.setProperty(ConstantUtil.ESTADO_TRANSACCION, simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.COD_RESPONSE,simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.DETALE_RESPONSE,simple("{{message.error.validator}}"))
				.setProperty(ConstantUtil.RESPONSE).body()
				.inOnly(ConstantUtil.ROUTE_AUDITORIA)
			.end()
			.onException(Exception.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en ejecución | Causa: ${exception.message}")
				.bean(ResponseHandler.class,"responseErrorAprovisionamiento({{message.codigo.failed}},message.detalle.aprovisionamiento.failed,[])")
				.setProperty(ConstantUtil.ESTADO_TRANSACCION, simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.COD_RESPONSE,simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.DETALE_RESPONSE,simple("{{message.detalle.aprovisionamiento.failed}}"))
				.setProperty(ConstantUtil.RESPONSE).body()
				.inOnly(ConstantUtil.ROUTE_AUDITORIA)
			.end()
			.setHeader(ConstantUtil.ENDPOINT, simple("{{endpoint.api.adquirir.paquete}}"))
			.marshal().json(JsonLibrary.Jackson)	
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Cargando datos base de auditoria")
			.process(new ConfigAuditoriaProcessor())
			.log(LoggingLevel.INFO,logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio la ruta")
			.log(LoggingLevel.INFO,logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: ${body}")
			.unmarshal().json(JsonLibrary.Jackson, RequestAprovisionamiento.class)
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Validando campos de entrada")
			.to("bean-validator:validator")
			.removeHeader("*")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje:Canal ${body}")
			.setProperty("min", simple("${body.data.min}"))
			.setProperty("offerId", simple("${body.data.offerId}"))
			.setProperty("hostId", simple("${body.data.hostId}"))
			//VM properties
			.setProperty("vm_user", simple("{{itel.vm.user}}"))
			.setProperty("vm_password", simple("{{itel.vm.password}}"))
			.setProperty("vm_method", simple("{{itel.vm.method}}"))
			.setProperty("vm_package", simple("{{itel.vm.package}}"))
			.setProperty("vm_codigoCobro", simple("{{itel.vm.codigo.cobro}}"))
			.setProperty("vm_paramDos", simple("{{itel.vm.param.dos}}"))
			.setProperty("vm_paramTres", simple("{{itel.vm.param.tres}}"))
			.setProperty("vm_ipOrigen", simple("{{itel.vm.ip.origen}}"))
			.to("velocity:ItelRequest.vm?contentCache=true&propertiesFile=velocity.properties")// 
			.setProperty(ConstantUtil.REQUEST_SE, simple("${body}"))
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Plantilla cargada | Detalle: \n ${body}")
			.setHeader(Exchange.HTTP_URI, simple("{{itel.aprovisionamiento.url}}"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Consumiendo servicio legado | Detalle: ${headers.CamelHttpUri}")
			.to("http4:AP")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Respuesta del servicio | Detalle: \n ${body}")
			.setProperty(ConstantUtil.RESPONSE_SE, simple("${body}",String.class))
			.process(e ->{
				String body = e.getIn().getBody(String.class);
				body = body.replaceAll("\"", "\\\"");
				e.getIn().setBody(body);
			})
			.setProperty(ConstantUtil.RESPNSE_CODE_SE, simple("${headers.CamelHttpResponseCode}"))
			.setProperty("resCodRespuesta", xpath("/*/*/*/MENSAJE/CODIGO_RESPUESTA/text()", String.class))
			.setProperty("resDesc", xpath("/*/*/*/MENSAJE/DESCRIPCION/text()", String.class))
			.removeHeaders("*")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
			
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Construyendo respuesta del servicio")
			.bean(ResponseHandler.class, "responseAprovisionamiento")
			.setProperty(ConstantUtil.RESPONSE, simple("${body}"))
			.inOnly(ConstantUtil.ROUTE_AUDITORIA)
		.end();
		
	}

}
