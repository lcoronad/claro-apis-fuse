package com.claro.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.claro.beans.ProxyBean;
import com.claro.beans.ResponseHandler;
import com.claro.dto.Request;
import com.claro.process.ConfigAuditoriaProcessor;
import com.claro.process.ErrorProcessor;
import com.claro.utils.AESEncryption;
import com.claro.utils.ConstantUtil;
import com.claro.utils.FuncionesUtileria;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RouteConsultaFacturacion extends RouteBuilder {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${canal.catalogo}")
	private String[] elementToSearch;

	@Override
	public void configure() throws Exception {

		Namespaces ns = new Namespaces("ns1", "http://schemas.datacontract.org/2004/07/BankPaymentManagement");
		Namespaces ns0 = new Namespaces("ns0", "http://schemas.xmlsoap.org/soap/envelope/");
		from(ConstantUtil.ROUTE_PARADIGMA).routeId("ROUTE_PARADIGMA").streamCaching("true")
				.onException(BeanValidationException.class)
					.handled(true)
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
					.setProperty(ConstantUtil.RESPONSE).body()
					.inOnly(ConstantUtil.ROUTE_AUDITORIA)
					.end()
				.onException(Exception.class).handled(true)
					.log(LoggingLevel.ERROR, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en ejecución | Causa: ${exception.message}")
					.bean(ResponseHandler.class,"errorValidatorBeans({{message.codigo.failed}},message.detalle.failed,'')")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
					.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
					.setProperty(ConstantUtil.COD_RESPONSE, simple("{{message.codigo.failed}}"))
					.setProperty(ConstantUtil.DETALE_RESPONSE, simple("{{message.detalle.failed}}"))
					.setProperty(ConstantUtil.RESPONSE).body()
					.inOnly(ConstantUtil.ROUTE_AUDITORIA)
				.end()
				.setProperty(ConstantUtil.API_NAME, simple("{{api.name}}"))
				.setProperty(ConstantUtil.LOG_USUARIO, simple("{{api.log.name}}"))
				.setHeader(ConstantUtil.ENDPOINT, simple("{{endpoint.api.consultar.factura}}"))
				.setProperty(ConstantUtil.NOMBRE_SE, constant("PARADIGMA"))
				.setProperty(ConstantUtil.NOMBRE_CANAL, constant(""))
				.setProperty(ConstantUtil.TIPO_PRODUCTO, simple("{{api.tipo.producto.pospago}}"))
				.marshal().json(JsonLibrary.Jackson)
				.process(new ConfigAuditoriaProcessor())
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio la ruta")
				.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: ${body}")
				.unmarshal().json(JsonLibrary.Jackson, Request.class)
				.setProperty(ConstantUtil.CANAL, simple("${body.data.canal}"))
				
				.to("bean-validator:validator")
				.removeHeader("*")
				.log(LoggingLevel.DEBUG, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje:Canal ${body.data.canal}")
				.log(LoggingLevel.DEBUG, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje:Cuenta Original ${body.data.getMin()}")
				.setProperty(ConstantUtil.MIN_APROVISIONAMIENTO, simple("${body.data.min}"))
				.process(e -> e.setProperty(ConstantUtil.NOMBRE_CANAL,this.elementToSearch[Integer.valueOf(e.getProperty(ConstantUtil.CANAL, String.class)) - 1]))
				.setProperty(ConstantUtil.REFERENCE_ID, simple("${body.data.idReferencia}"))
				.setHeader(ConstantUtil.REFERENCE_ID, simple("${body.data.idReferencia.isEmpty()}"))
				.setHeader("minexist", simple("${body.data.min.isEmpty()}"))
				.log(LoggingLevel.DEBUG, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje:Referencia id:  ${body.data.idReferencia}")

				.log(LoggingLevel.DEBUG, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje:Referencia es vacia existe:  ${headers.referenceId}")
				.choice().when(simple("${exchangeProperty.min} != '' "))
				.log(LoggingLevel.DEBUG, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje:CONSUMO DE SERVICIO POR MIN")
				.setProperty("cuenta")
				.method(AESEncryption.class, "encrypt(${exchangeProperty.min},{{paradigma.key}})")
				.setProperty("origin", simple("{{paradigma.origin}}"))
				.log(LoggingLevel.DEBUG, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Cuenta Encrypt ${exchangeProperty.cuenta}")
				.to("velocity:jsonparadigma.vm?contentCache=true&propertiesFile=velocity.properties")
				.setProperty(ConstantUtil.REQUEST_SE).body()
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Request paradigma ${body}")
				.removeHeader("*")
				.bean(ProxyBean.class)
//					.setBody(constant("{\"d\":\"{\\\"error\\\":{\\\"isError\\\": false, \\\"msg\\\":\\\"\\\",\\\"exception\\\":\\\"\\\"},\\\"factura\\\": [{\\\"numeroFactura\\\":5327580128, \\\"valorPagar\\\": 49049.99, \\\"fechaPagoOportuno\\\": \\\"03-Dic-2019\\\", \\\"nombreUsuario\\\": \\\"Sr. JUAN CARLOS SELLEVOLD\\\", \\\"referenciaPago\\\": \\\"1006705257\\\", \\\"direcci�n\\\": \\\"CR. 4A Nro. 27-108\\\"}]}\"}"))
//					.setHeader("statuscode",constant("200"))
				.log(LoggingLevel.DEBUG, logger,"Proceso: ${exchangeProperty.procesoId} | Mensaje: Respuesta Servicio Paradigma \n ${body}")
				.setProperty(ConstantUtil.RESPONSE_SE).body()
				.setProperty(ConstantUtil.RESPNSE_CODE_SE).header("statuscode")
				.to(ConstantUtil.ROUTE_VALIDATOR_FACTURA).otherwise()
				.log(LoggingLevel.DEBUG, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: MIN VACIO, CONSUMO POR REFERENCIA")
				.to(ConstantUtil.ROUTE_CONSULTA_FACTORAMP).end().inOnly(ConstantUtil.ROUTE_AUDITORIA).end();

		from(ConstantUtil.ROUTE_VALIDATOR_FACTURA).routeId("ROUTE_VALIDATOR").streamCaching("true")
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Validación ${headers.statuscode}")
				.errorHandler(noErrorHandler()).choice().when()
				.spel("#{!request.headers['statuscode'].contains('200')}")
				.log(LoggingLevel.ERROR, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en servicio Paradigma | Causa: Estado del servicio ${headers.statuscode}")
				.throwException(Exception.class, "Error en el servicio status ${headers.statuscode} ").when()
				.spel("#{!request.headers['statuscode'].contains('200') && !request.headers['referenceId']}")
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en servicio Paradigma | Causa: Estado del servicio ${headers.statuscode}")
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: SE PROCEDE A CONSULTAR EN MP")
				.to(ConstantUtil.ROUTE_CONSULTA_FACTORAMP).otherwise()
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Response paradigma ${body}")
				.setBody().jsonpath("$.d").setHeader("iserror").jsonpath("$.error.isError")
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: data Paradigma ${body}")
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: hay error?  ${headers.iserror}")
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Referencia esta vacia ?  ${headers.referenceId}")
				.choice().when(simple("${headers.iserror} == 'true' && ${headers.referenceId} == false "))
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: No encontro información en paradigma , procede a consultar MOTOR DE PAGOS")
				.to(ConstantUtil.ROUTE_CONSULTA_FACTORAMP).endChoice().otherwise()
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio a construir respuesta")
				.bean(ResponseHandler.class)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
				.setProperty(ConstantUtil.RESPONSE).body()
				.setProperty(ConstantUtil.COD_RESPONSE, simple("{{message.codigo.ok}}"))
				.setProperty(ConstantUtil.DETALE_RESPONSE, simple("{{message.detalle.ok}}"))
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Finalizo consumo  ${body}")
				.endChoice().end()

				.end();

		from(ConstantUtil.ROUTE_CONSULTA_FACTORAMP).routeId("GETFACTURAMP").streamCaching("true")
				.onException(Exception.class)
					.log(LoggingLevel.ERROR, logger,
							"Proceso: ${exchangeProperty.procesoId} | Mensaje: Error en consumo de servicio | Causa: ${exception.message}")
					.handled(true)
					.bean(ResponseHandler.class,
							"responseMP({{message.codigo.failed}},message.detalle.cf.failed,${headers.data})")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
					.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
					.setProperty(ConstantUtil.COD_RESPONSE, simple("{{message.codigo.failed}}"))
					.setProperty(ConstantUtil.DETALE_RESPONSE, simple("{{message.detalle.cf.failed}}"))
					.setProperty(ConstantUtil.RESPONSE).body()
					.inOnly(ConstantUtil.ROUTE_AUDITORIA)
				.end()
				.setHeader("data", constant("{}"))
				.setProperty(ConstantUtil.NOMBRE_SE, constant("ITEL"))
				.log(LoggingLevel.INFO, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio a consumir el servicio de MOTOR DE PAGOS")
				.process(e ->{
					ObjectMapper objectMapper = new ObjectMapper();
					Request request = objectMapper.readValue(e.getProperty(ConstantUtil.REQUEST, String.class), Request.class);
					logger.info("Se encontro el dto: {}", request.getData().bankCode);
					logger.info("Se encontro el dto: {}", request.getData().paymentReceptionDate);
					e.setProperty("bankcode", request.getData().bankCode);
					e.setProperty("fechaT", request.getData().paymentReceptionDate);
				})
				.setProperty("username", simple("{{mp.getfactura.usuario}}"))
				.setProperty("password", simple("{{mp.getfactura.password}}"))
				.setProperty("companyid", simple("{{mp.getfactura.companyid}}"))
				.setProperty(ConstantUtil.REFERENCE_ID).method(FuncionesUtileria.class, "completeIdReference(${exchangeProperty.referenceId})")
				.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Valor de bankcode ${exchangeProperty.bankcode}")
				.to("velocity:xmlmotorpagosfacturaconsulta.vm?contentCache=true&propertiesFile=velocity.properties")
				.setProperty(ConstantUtil.REQUEST_SE).body()
				.log(LoggingLevel.DEBUG, logger,
						"Proceso: ${exchangeProperty.procesoId} | Mensaje: Plantilla cargada | Detalle: \n ${body}")
				.setHeader(Exchange.HTTP_URI, simple("{{mp.getfactura.url}}"))
				.to("http4:dummy?httpClient.connectTimeout=35000&httpClient.socketTimeout=35000&throwExceptionOnFailure=false")
				
				.setProperty(ConstantUtil.RESPONSE_SE).body(String.class)
				.setProperty(ConstantUtil.RESPNSE_CODE_SE).header(Exchange.HTTP_RESPONSE_CODE)
					.log(LoggingLevel.DEBUG, logger,
							"Proceso: ${exchangeProperty.procesoId} | Mensaje: Respnse web service | Detalle: \n  ${body}")
					.log(LoggingLevel.DEBUG, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Response SE | \n ${exchangeProperty.responseSE}")
					.choice()
						.when().xpath("/*/*/*/faultcode/text()",ns0)
							.setHeader("errorCode").xpath("/*/*/*/*/*/errorCode/text()", ns)
							.setHeader("errorMessage").xpath("/*/*/*/*/*/stackTrace/text()", ns)
							.process(exchange -> {
								String data = "{\"data\":{ \"errorCode\" :\""
										+ exchange.getIn().getHeader("errorCode", String.class) + "\" , \"errorMessage\" : \" "
										+ exchange.getIn().getHeader("errorMessage", String.class) + "\"}}";
								exchange.getIn().setHeader("data", data);
								})
							.throwException(Exception.class, "Error en servicio Motor de Pagos")
						.when().xpath("/*/*/*/*/ns1:errorCode/text()", ns)
							.setHeader("errorCode").xpath("/*/*/*/*/ns1:errorCode/text()", ns)
							.setHeader("errorMessage").xpath("/*/*/*/*/ns1:errorMessage/text()", ns)
							.process(exchange -> {
								String data = "{\"data\":{ \"errorCode\" :\""
										+ exchange.getIn().getHeader("errorCode", String.class) + "\" , \"errorMessage\" : \" "
										+ exchange.getIn().getHeader("errorMessage", String.class) + "\"}}";
								exchange.getIn().setHeader("data", data);
							})
		
							.throwException(Exception.class, "Error en servicio Motor de Pagos")
							.end()
	
					.setHeader("paymentAmount").xpath("/*/*/*/*/ns1:paymentAmount/text()", ns)
					.log(LoggingLevel.DEBUG, logger,
							"Proceso: ${exchangeProperty.procesoId} | Mensaje: xpath encontrado = ${headers.paymentAmount}")
					.process(exchange -> {
						String data = "{\"data\":{ \"paymentAmount\" :\""
								+ exchange.getIn().getHeader("paymentAmount", String.class) + "\"}}";
						exchange.getIn().setHeader("data", data);
					})
					.bean(ResponseHandler.class, "responseMP({{message.codigo.ok}},message.detalle.ok,${headers.data})")
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
					.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8))
					.setProperty(ConstantUtil.COD_RESPONSE, simple("{{message.codigo.ok}}"))
					.setProperty(ConstantUtil.DETALE_RESPONSE, simple("{{message.detalle.ok}}"))
					.setProperty(ConstantUtil.RESPONSE).body()
					.log(LoggingLevel.DEBUG, logger,
							"Proceso: ${exchangeProperty.procesoId} | Mensaje: Response in json | Detalle: \n  ${body}")
					.inOnly(ConstantUtil.ROUTE_AUDITORIA)
				.end();

	}

}
