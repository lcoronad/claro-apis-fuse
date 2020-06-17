/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.claro.esb.authentication.routes;

import java.net.ConnectException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.TypeConversionException;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.stereotype.Component;

import com.claro.esb.authentication.configurator.ConfigurationRoute;
import com.claro.esb.authentication.model.CustomException;
import com.claro.esb.authentication.model.Request;
import com.claro.esb.authentication.model.Response;
import com.claro.esb.authentication.transformations.AggregatorBean;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class TransformationRoute extends ConfigurationRoute {
	
	@Autowired
	private AggregatorBean aggregatorBean;
	
	private static final String SUCCESS_RESPONSE = "direct:buildSuccessResponse";
	private static final String AUTH_ERROR_MSG = "Se ha producido un error en la autenticación";
	private Logger log =	 LoggerFactory.getLogger(TransformationRoute.class);
	
	public void configure() throws Exception {
		super.configure();
		
		JacksonDataFormat format = new JacksonDataFormat(Request.class);
		JacksonDataFormat response = new JacksonDataFormat(Response.class);
		
		onException(TypeConversionException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, log, "Ha ocurrido una excepcion TypeConversionException: " + exceptionMessage())
	        .setHeader("CamelHttpResponseCode", simple("400"))
	        .setHeader("codigoRespuesta", constant("FAILED"))
	        .setHeader("mensajeRespuesta", constant(AUTH_ERROR_MSG))
	        .bean("transformations", "buildResponse")
	        .marshal(response);
		
		onException(ConnectException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, log, "Ha ocurrido una excepcion ConnectException: " + exceptionMessage())
	        .setHeader("CamelHttpResponseCode", simple("400"))
	        .setHeader("codigoRespuesta", constant("FAILED"))
	        .setHeader("mensajeRespuesta", constant(AUTH_ERROR_MSG))
	        .bean("transformations", "buildResponse")
	        .marshal(response);
		
		onException(CustomException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, log, "Ha ocurrido un error de validacion - Error: " + exceptionMessage())
	        .setHeader("CamelHttpResponseCode", simple("200"))
	        .setHeader("codigoRespuesta", constant("OK"))
	        .setHeader("mensajeRespuesta", exceptionMessage())
	        .bean("transformations", "buildResponse")
	        .to("direct:audit_route")
	        .log(LoggingLevel.INFO, log, "Sending to audit route: ${body}")
			.inOnly("seda:amqAuditRoute")
	        .marshal(response);
		
		onException(JsonParseException.class, InvalidFormatException.class, UncategorizedJmsException.class)
	        .handled(true)
	        .log(LoggingLevel.ERROR, log, "Ha ocurrido una excepcion: " + exceptionMessage())
	        .setHeader("CamelHttpResponseCode", simple("400"))
	        .setHeader("codigoRespuesta", constant("FAILED"))
	        .setHeader("mensajeRespuesta", constant(AUTH_ERROR_MSG))
	        .bean("transformations", "buildResponse")
	        .marshal(response);
		
		onException(BeanValidationException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, log, "Ha ocurrido una excepcion (BeanValidationException): " + exceptionMessage())
			.setHeader("CamelHttpResponseCode", simple("200"))
	        .setHeader("codigoRespuesta", constant("OK"))
	        .setHeader("mensajeRespuesta", constant(AUTH_ERROR_MSG))
	        .process("buildResponse")
	        .bean("transformations", "buildResponse")
	        .to("direct:audit_route")
	        .log(LoggingLevel.INFO, log, "Sending to audit route: ${body}")
			.inOnly("seda:amqAuditRoute")
	        .marshal(response);
		
		from("direct:transformationRoute").routeId("authentication_transformation")
			.log(LoggingLevel.DEBUG, log, "NombreCliente: ${headers.NombreCliente}")
			.log(LoggingLevel.DEBUG, log, "ClientID: ${headers.ClientID}")
			.log(LoggingLevel.INFO, log, "Start transformation route")
			.setHeader("api_name", constant("authentication"))
			.setHeader("userIsActive", constant("Inactivo"))
			.unmarshal(format)
			.setHeader("sentRequest", simple("${body}"))
			.bean("transformations", "setAuthenticationType")
			.to("bean-validator://x")
			.log(LoggingLevel.DEBUG, log, "Initial validation ok")
			.bean("transformations", "validateFields")
			.log(LoggingLevel.DEBUG, log, "AuthType: ${headers.authenticationType}")
			.choice()
				.when().simple("${headers.authenticationType} == 'multiple'")
					.log(LoggingLevel.DEBUG, log, "Multiple authentication detected")
					.to("direct:multipleAuths")
				.when().simple("${headers.authenticationType} == 'accountNumber'")
					.log(LoggingLevel.DEBUG, log, "authentication by account")
					.to("direct:account")
				.when().simple("${headers.authenticationType} == 'min'")
					.log(LoggingLevel.DEBUG, log, "authentication by min")
					.to("direct:min")
				.when().simple("${headers.authenticationType} == 'typeDoc_numDoc'")
					.log(LoggingLevel.DEBUG, log, "authentication type and num doc")
					.to("direct:typedoc_numdoc")
				.when().simple("${headers.authenticationType} == 'user_password'")
					.log(LoggingLevel.DEBUG, log, "authentication by user and password")
					.to("direct:user_password")
				.otherwise()
					.log(LoggingLevel.DEBUG, log, "no authentication type selected")
				.end()
			.marshal(format)
		.end();
		
		// User Password
		from("direct:user_password").routeId("user_password_route")
			.choice()
				.when().simple("${headers.isUserAuth}")
					.log(LoggingLevel.DEBUG, log, "User password authentication")
					.setHeader("resultUserPassAuth", constant(false))
					.to("direct:restProducerRouteESB")
					.choice()
						.when(xpath("/*/*/*/faultcode"))
							.log(LoggingLevel.INFO, log, "Cliente No encontrado en SSO")
							.setHeader("resultUserPassAuth", constant(false))
							.setHeader("codigoRespuesta", constant("OK"))
					        .setHeader("mensajeRespuesta", constant("Usuario y/o Contraseña Inválidos"))
					        .bean("transformations", "buildResponse")
					        .to("direct:audit_route")
							.log(LoggingLevel.INFO, log, "Sending to audit route: ${body}")
							.inOnly("seda:amqAuditRoute")
					.otherwise()
						.log(LoggingLevel.INFO, log, "Cliente Encontrado en SSO")
						.setHeader("resultUserPassAuth", constant(true))
						.setHeader("userIsActive", constant("Activo"))
						.to(SUCCESS_RESPONSE)
					.end()
			.end()
		.end();
		
		// Min
		from("direct:min").routeId("min_route")
		.choice()
			.when().simple("${headers.isMinAuth}")
				.log(LoggingLevel.DEBUG, log, "Min authentication: ${body.data.min}")
				.setHeader("resultMinAuth", constant(false))
				.setHeader("tipoConsulta", constant("TL"))
				.to("direct:BSCSDatabaseRoute")
				.bean("transformations", "getBSCSMinResponse")
				.log(LoggingLevel.DEBUG, log, "Min response: ${headers.minResponse}")
				.choice()
					.when(simple("${headers.minResponse} == 'Activo'"))
						.setHeader("resultMinAuth", constant(true))
						.setHeader("userIsActive", constant("Activo"))
						.to(SUCCESS_RESPONSE)
					.otherwise()
						.log(LoggingLevel.INFO, log, "No se encontro usuario en BSCS")
						.setHeader("resultMinAuth", constant(false))
						.setHeader("codigoRespuesta", constant("OK"))
				        .setHeader("mensajeRespuesta", constant("Cliente no encontrado"))
				        .setHeader("userIsActive", constant("Inactivo"))
				        .bean("transformations", "buildResponse")
				        .to("direct:audit_route")
						.log(LoggingLevel.INFO, log, "Sending to audit route: ${body}")
						.inOnly("seda:amqAuditRoute")
			.end()
		.end();
		
		// Type doc Num doc
		from("direct:typedoc_numdoc").routeId("typedoc_numdoc_route")
		.choice()
			.when().simple("${headers.isIdAuth}")
				.log(LoggingLevel.DEBUG, log, "Type Doc Num Doc authentication")
				.setHeader("tipoConsulta", constant("DO"))
				.setHeader("queryValue", simple("${body.data.numeroIdentificacion}"))
				.setHeader("resultIdAuth", constant(false))
				.to("direct:restProducerRouteRR")
				.choice()
					.when(xpath("/*/*/*/return[MENSAJEERR/text() != '']"))
						.log(LoggingLevel.INFO, log, "Cliente no encontrado en RR - consultando en BD ONYX")
						.setHeader("queryType", constant("ND"))
						.setHeader("resultIdAuth", constant(false))
						.to("direct:onyxDatabaseRoute")
						.setHeader("resultIdAuth", constant(true))
						.bean("transformations", "isActiveFromOnyx")
						.to(SUCCESS_RESPONSE)
					.otherwise()
						.log(LoggingLevel.INFO, log, "Cliente ENCONTRADO en RR")
						.setHeader("resultIdAuth", constant(true))
						.setHeader("userIsActive", constant("Activo"))
						.to(SUCCESS_RESPONSE)
			.end()
		.end();
		
		// account
		from("direct:account").routeId("account_route")
			.choice()
				.when().simple("${headers.isAccountAuth}")
					.log(LoggingLevel.DEBUG, log, "Account authentication: ${body.data.cuenta}")
					.setHeader("resultAccountAuth", constant(false))
					.setHeader("tipoConsulta", constant("SU"))
					.setHeader("queryType", constant("NC"))
					.setHeader("queryValue", simple("${body.data.cuenta}"))
					.to("direct:restProducerRouteRR")
					.log(LoggingLevel.INFO, log, "Status Code: ${headers.CamelHttpResponseCode}")
					.choice()
						.when(simple("${headers.CamelHttpResponseCode} == 500"))
							.log(LoggingLevel.INFO, log, "Cliente no encontrado en RR - consultando en BD ONYX")
							.to("direct:onyxDatabaseRoute")
							.setHeader("resultAccountAuth", constant(true))
							.to(SUCCESS_RESPONSE)
						.otherwise()
							.choice()
								.when(xpath("/*/*/*/return[MENSAJEERR/text() != '']"))
									.log(LoggingLevel.INFO, log, "Cliente no encontrado en RR - consultando en BD ONYX")
									.to("direct:onyxDatabaseRoute")
									.setHeader("resultAccountAuth", constant(true))
									.setHeader("userIsActive", constant("Activo"))
									.to(SUCCESS_RESPONSE)
								.otherwise()
									.log(LoggingLevel.INFO, log, "Cliente ENCONTRADO en RR")
									.setHeader("userIsActive", constant("Activo"))
									.setHeader("resultAccountAuth", constant(true))
									.to(SUCCESS_RESPONSE)
							.endChoice()
					.end()
			.end()
		.end();
		
		from("direct:multipleAuths")
			.multicast(aggregatorBean)
			.to("direct:account", "direct:min", "direct:typedoc_numdoc", "direct:user_password")
			.end()
			.bean("transformations","buildMultipleAuthResponse")
			.choice()
				.when().simple("${headers.multipleOk}")
					.to(SUCCESS_RESPONSE)
				.otherwise()
					.setHeader("codigoRespuesta", constant("OK"))
			        .setHeader("mensajeRespuesta", constant("Error en los datos proporcionados"))
			        .bean("transformations", "buildResponse")
		.end();
		
		from(SUCCESS_RESPONSE)
			.setHeader("codigoRespuesta", constant("OK"))
	        .setHeader("mensajeRespuesta", constant("Usuario Válido"))
	        .choice()
	        	.when().simple("${headers.userIsActive} == 'Inactivo'")
	        		.setHeader("mensajeRespuesta", constant("Usuario no encontrado"))
	        	.otherwise()
	        		.log(LoggingLevel.DEBUG, log, "Usuario encontrado y activo")
	        .end()
	        .bean("transformations", "buildResponse")
	        .to("direct:audit_route")
			.log("Sending to audit route: ${body}")
			.inOnly("seda:amqAuditRoute")
        .end();
		
		from("direct:audit_route").routeId("audit_route")
		.choice()
			.when().simple("${headers.authenticationType} == 'accountNumber'")
				.log("Audit Account")
				.bean("transformations", "auditAccount")
			.when().simple("${headers.authenticationType} == 'min'")
				.log("Audit Min")
				.bean("transformations", "auditMin")
			.when().simple("${headers.authenticationType} == 'typeDoc_numDoc'")
				.log("Audit Type and Num Doc")
				.bean("transformations", "auditId")
			.when().simple("${headers.authenticationType} == 'user_password'")
				.log("Audit User Password")
				.bean("transformations", "auditUserPass")
			.end()
		.end();
		
		from("direct:healthcheckResponse")
			.setBody(constant("OK"));
	}
}