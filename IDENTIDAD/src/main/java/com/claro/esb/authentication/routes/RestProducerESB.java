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



import java.net.UnknownHostException;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.claro.esb.authentication.configurator.ConfigurationRoute;
import com.claro.esb.authentication.model.Response;
import com.claro.esb.authentication.properties.WSSSO;

@Component
public class RestProducerESB extends ConfigurationRoute {

	private Logger log = LoggerFactory.getLogger(RestProducerESB.class);
	
	@Autowired
	private WSSSO soapConfig;

	@Override
	public void configure() throws Exception {
		super.configure();
		
		JacksonDataFormat response = new JacksonDataFormat(Response.class);

		onException(HttpOperationFailedException.class)
			.log(LoggingLevel.ERROR, log, "Error HttpOperationFailedException controlado... continuando")
			.continued(true);
		
		onException(UnknownHostException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, log, "Ha ocurrido un error en el consumo del WS de SSO: " + exceptionMessage())
			.setHeader("CamelHttpResponseCode", simple("400"))
	        .setHeader("codigoRespuesta", constant("FAILED"))
	        .setHeader("mensajeRespuesta", constant("Se ha producido un error en la autenticación"))
	        .bean("transformations", "buildResponse")
	        .marshal(response)
			.end();
		
		from("direct:restProducerRouteESB").routeId("producerRouteRestESB")
			.bean("transformations", "loadESBInfo")
			.setHeader("contrasenaAutenticacion", constant(soapConfig.getContrasenaAutenticacion()))
			.setHeader("tipoCanalID", constant(soapConfig.getTipoCanalID()))
			.setHeader("usuarioAutenticacion", constant(soapConfig.getUsuarioAutenticacion()))
			.setHeader(Exchange.HTTP_METHOD, constant(soapConfig.getMethod()))
			//.setHeader(Exchange.HTTP_URI, constant(soapConfig.getUrl()))
			.setHeader("SOAPAction", constant(soapConfig.getSoapAction()))
			.setHeader("Content-Type", constant(soapConfig.getContentType()))
			.log(LoggingLevel.DEBUG, log, soapConfig.getUrl())
			.to("velocity:ESBTemplate.vm")
			.log(LoggingLevel.INFO, log, "Invoking request ESB web service ... ${body}")
			.bean("transformations", "proxy").id("consumeSSOWS")
			.log(LoggingLevel.DEBUG, log, "respuesta: ${body}")
		.end();
	}

}
