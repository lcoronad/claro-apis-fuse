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

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.claro.esb.authentication.configurator.ConfigurationRoute;
import com.claro.esb.authentication.model.Response;
import com.claro.esb.authentication.properties.WSRR;

@Component
public class RestProducerRR extends ConfigurationRoute {
	private static final String AUTH_ERROR_MSG = "Se ha producido un error en la autenticación";
	private Logger log = LoggerFactory.getLogger(RestProducerRR.class);

	@Autowired
	private WSRR soapConfig;

	@Override
	public void configure() throws Exception {
		super.configure();
		JacksonDataFormat response = new JacksonDataFormat(Response.class);
		
		onException(HttpHostConnectException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, log, "Ha ocurrido una excepcion HttpHostConnectException: " + exceptionMessage())
	        .setHeader("CamelHttpResponseCode", simple("400"))
	        .setHeader("codigoRespuesta", constant("FAILED"))
	        .setHeader("mensajeRespuesta", constant(AUTH_ERROR_MSG))
	        .bean("transformations", "buildResponse")
	        .marshal(response)
		.end();
		
		
		from("direct:restProducerRouteRR").routeId("producerRouteRestRR")
			.log(LoggingLevel.INFO, log, "start direct:restProducerRouteRR with body: ${body}")
			.bean("transformations", "loadRRInfo")
			.log(LoggingLevel.DEBUG, log, "Loaded info")
			.to("velocity:RRTemplate.vm")
			.setHeader(Exchange.HTTP_METHOD, constant(soapConfig.getMethod()))
			.setHeader(Exchange.HTTP_URI, constant(soapConfig.getUrl()))
			.setHeader("Content-Type", constant(soapConfig.getContentType()))
			.log(LoggingLevel.INFO, log, "Invoking web service RR Claro... ${body}")
			.to("http4:RRWS?throwExceptionOnFailure=false")
			.log(LoggingLevel.DEBUG, log, "respuesta: ${body}")
		.end();
	}

}
