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
import com.claro.beans.TransformationBean;
import com.claro.dto.RequestAplicarCargo;
import com.claro.process.ConfigAuditoriaProcessor;
import com.claro.process.ErrorProcessor;
import com.claro.utils.ConstantUtil;

@Component
public class RouteAplicarCargo  extends RouteBuilder{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void configure() throws Exception {
		
		from(ConstantUtil.ROUTE_APLICAR_CARGO).routeId("ROUTE_APLICAR_CARGO").streamCaching("true")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Inicio de operacion 'Aplicar Cargo'")
			.setProperty(ConstantUtil.API_NAME, simple("{{api.name}}"))
			.setProperty(ConstantUtil.LOG_USUARIO, simple("{{api.log.name}}"))
			.setProperty(ConstantUtil.CANAL).jsonpath("$.data.canal")
			.setProperty(ConstantUtil.NOMBRE_SE, simple("{{itel.aplicar.cargo.nombre}}"))
			.setProperty(ConstantUtil.TIPO_PRODUCTO, simple("{{api.tipo.producto.pospago}}"))
			// set vm properties
			.setProperty("APUsername", simple("{{itel.aplicar.cargo.username}}"))
			.setProperty("APPassword", simple("{{itel.aplicar.cargo.password}}"))
			.setProperty("APBankCode").jsonpath("$.data.bankCode")
			.setProperty("APBankPaymentID").jsonpath("$.data.bankPaymentID")
//			.setProperty("APBarCode").jsonpath("$.data.barCode")
			.setProperty("APCompanyID", simple("{{itel.aplicar.cargo.companyid}}"))
//			.setProperty("APInvoice").jsonpath("$.data.invoice")
			.setProperty("APPaymentAmount").jsonpath("$.data.valor")
			.setProperty("APPaymentMethod").jsonpath("$.data.paymentMethod")
			.setProperty("APPaymentReference").jsonpath("$.data.idReferencia")
			.setProperty("APPaymentReceptionDate").jsonpath("$.data.paymentReceptionDate")
			.setProperty("APPaymentAccountingDate").jsonpath("$.data.paymentAccountingDate")
			.to("direct:getNombreCanal")
			.to("direct:getNombrePaymenMethod")
			.onException(BeanValidationException.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en la validación de los campos | Causa: ${exception.message}")
				.setProperty(ConstantUtil.EXCEPTION, simple("${exception.message}"))
				.process(new ErrorProcessor())
				.log(LoggingLevel.ERROR, logger,"Proceso: ${exchangeProperty.procesoId} | Mensaje: Se encontro una excepción en el mensaje de entrada | Causa:${exchangeProperty.listErrorDetails}")
				.bean(ResponseHandler.class,"errorValidatorBeans({{message.codigo.failed}},message.error.validator,${exchangeProperty.listErrorDetails})")
				.setProperty(ConstantUtil.ESTADO_TRANSACCION, simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.COD_RESPONSE,simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.DETALE_RESPONSE,simple("{{message.detalle.aplicar.cargo.failed}}"))
				.setProperty(ConstantUtil.RESPONSE).body()
				.inOnly(ConstantUtil.ROUTE_AUDITORIA)
			.end()
			.onException(Exception.class)
				.handled(true)
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en ejecución | Causa: ${exception.message}")
				.bean(ResponseHandler.class,"responseErrorAprovisionamiento({{message.codigo.failed}},message.detalle.aplicar.cargo.failed,[])")
				.setProperty(ConstantUtil.ESTADO_TRANSACCION, simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.COD_RESPONSE,simple("{{message.codigo.failed}}"))
				.setProperty(ConstantUtil.DETALE_RESPONSE,simple("{{message.detalle.aplicar.cargo.failed}}"))
				.setProperty(ConstantUtil.RESPONSE).body()
				.inOnly(ConstantUtil.ROUTE_AUDITORIA)
			.end()
			.setHeader(ConstantUtil.ENDPOINT, simple("{{endpoint.api.pagar.factura}}"))
			.marshal().json(JsonLibrary.Jackson)
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Cargando datos base de auditoria")
			.process(new ConfigAuditoriaProcessor())
			.unmarshal().json(JsonLibrary.Jackson, RequestAplicarCargo.class)
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Validanto datos de entrada")
			.to("bean-validator:validator")
			.removeHeader("*")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje:Canal ${body}")
			.bean(TransformationBean.class)
			.to("velocity:AplicarPagoRequest.vm?contentCache=true&propertiesFile=velocity.properties")// 
			.setProperty(ConstantUtil.REQUEST_SE, simple("${body}"))
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Plantilla cargada | Detalle: \n ${body}")
			.setHeader(Exchange.HTTP_URI, simple("{{itel.aplicar.cargo.url}}"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Consumiendo WS | Uri: ${headers.CamelHttpUri}")
			.to("http4:AP")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Respuesta del servicio | Detalle: \n ${body}")
			.setProperty(ConstantUtil.RESPONSE_SE, simple("${body}"))
			.setProperty(ConstantUtil.RESPNSE_CODE_SE, simple("${headers.CamelHttpResponseCode}"))
			.setHeader("xpathTest", xpath("/*/*/*/*[local-name()='isValidConfirmation']/text()", String.class))
			.choice()
				.when().simple("${headers.xpathTest} == true")
					.setProperty("bankCode").xpath("/*/*/*/*/*[local-name()='bankCode']/text()", String.class)
					.setProperty("bankPaymentConfirmationID").xpath("/*/*/*/*/*[local-name()='bankPaymentConfirmationID']/text()", String.class)
					.setProperty("bankPaymentID").xpath("/*/*/*/*/*[local-name()='bankPaymentID']/text()", String.class)
					.process(exchange ->{
						String data = "{\r\n" + 
								"  \"data\":{ \r\n" + 
								"    \"bankCode\" : \""+exchange.getProperty("bankCode", String.class) + "\",\r\n" + 
								"    \"bankPaymentConfirmationID\" : \""+exchange.getProperty("bankPaymentConfirmationID", String.class)+"\",\r\n" + 
								"    \"bankPaymentID\" : \""+exchange.getProperty("bankPaymentID", String.class)+"\"\r\n" + 
								"  }\r\n" + 
								"}";
						exchange.getIn().setHeader("data", data);
					})
				.endChoice()
				.otherwise()
					.setProperty("errorCode").xpath("/*/*/*/*/*[local-name()='errorCode']/text()", String.class)
					.setProperty("errorMessage").xpath("/*/*/*/*/*[local-name()='errorMessage']/text()", String.class)
					.process(exchange ->{
						String data = "{\r\n" + 
								"  \"data\":{ \r\n" + 
								"    \"errorCode\" : \""+exchange.getProperty("errorCode", String.class)+"\",\r\n" + 
								"    \"errorMessage\" : \""+exchange.getProperty("errorMessage", String.class)+"\"\r\n" + 
								"  }\r\n" + 
								"}";
						exchange.getIn().setHeader("data", data);
					})
				.endChoice()
			.end()
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Construyendo respuesta del servicio")
			.bean(ResponseHandler.class,"responseAplicarCargo")
			// -------------------------------------
			.removeHeaders("*")
			.setProperty(ConstantUtil.DETALE_RESPONSE,simple("{{message.detalle.aplicar.cargo.ok}}"))
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
			.setProperty(ConstantUtil.ESTADO_TRANSACCION, simple("{{message.codigo.ok}}"))
			.setProperty(ConstantUtil.RESPONSE, simple("${body}"))
			.inOnly(ConstantUtil.ROUTE_AUDITORIA)
		.end();
		
		from("direct:getNombreCanal").routeId("GET_NOMBRE_CANAL")
			.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Obteniendo el nombre del canal")
			.choice()
				.when().simple("${exchangeProperty.canal} == 1")
					.setProperty(ConstantUtil.NOMBRE_CANAL, simple("{{canal.aplicar.cargo.uno}}"))
				.endChoice()
				.when().simple("${exchangeProperty.canal} == 2")
					.setProperty(ConstantUtil.NOMBRE_CANAL, simple("{{canal.aplicar.cargo.dos}}"))
				.endChoice()
				.when().simple("${exchangeProperty.canal} == 3")
					.setProperty(ConstantUtil.NOMBRE_CANAL, simple("{{canal.aplicar.cargo.tres}}"))
				.endChoice()
				.otherwise()
					.setProperty(ConstantUtil.NOMBRE_CANAL, constant(""))
					.log("Error: Canal invalido")
				.endChoice()
			.end()
		.end();
		
		from("direct:getNombrePaymenMethod").routeId("GET_PAYMENT_METHOD")
		.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Obteniendo el nombre del metodo de pago")
		.choice()
			.when().simple("${exchangeProperty.APPaymentMethod} == 1")
				.setProperty(ConstantUtil.METODO_PAGO, simple("{{payment.method.one}}"))
			.endChoice()
			.when().simple("${exchangeProperty.APPaymentMethod} == 2")
				.setProperty(ConstantUtil.METODO_PAGO, simple("{{payment.method.two}}"))
			.endChoice()
			.when().simple("${exchangeProperty.APPaymentMethod} == 3")
				.setProperty(ConstantUtil.METODO_PAGO, simple("{{payment.method.three}}"))
			.endChoice()
			.otherwise()
				.setProperty(ConstantUtil.METODO_PAGO, constant(""))
				.log("Error: Metodo de pago desconocido")
			.endChoice()
		.end()
	.end();
	}

}
