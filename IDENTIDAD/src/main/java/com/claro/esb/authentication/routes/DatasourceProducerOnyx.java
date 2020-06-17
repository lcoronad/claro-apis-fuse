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

import org.apache.camel.LoggingLevel;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.claro.esb.authentication.configurator.ConfigurationRoute;
import com.claro.esb.authentication.model.Response;
import com.claro.esb.authentication.properties.DatasourceOnyx;
import com.microsoft.sqlserver.jdbc.SQLServerException;

@Component
public class DatasourceProducerOnyx extends ConfigurationRoute {
	private Logger log = LoggerFactory.getLogger(DatasourceProducerOnyx.class);
	
	@Autowired
	private DatasourceOnyx consumerBase;
	
	public void configure() throws Exception{
		super.configure();
		JacksonDataFormat response = new JacksonDataFormat(Response.class);
		
		onException(SQLServerException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, log, "Exception captured: " + exceptionMessage())
	        .setHeader("CamelHttpResponseCode", simple("400"))
	        .setHeader("codigoRespuesta", constant("FAILED"))
	        .setHeader("mensajeRespuesta", constant("Se ha producido un error en la autenticación"))
	        .process("buildResponse")
	        .bean("transformations", "buildResponse")
	        .to("direct:audit_route")
	        .log(LoggingLevel.DEBUG, log, "Sending to audit route: ${body}")
			.inOnly("seda:amqAuditRoute")
	        .marshal(response);
		
		
		from("direct:onyxDatabaseRoute").routeId("authentication_database_onyx")
			.log(LoggingLevel.DEBUG, log, "on direct:onyxDatabaseRoute - ${headers.queryType} - ${headers.queryValue}")
			.setHeader("vchTipoDocumento", simple("${headers.queryType}"))
			.setHeader("vchNoDocumento", simple("${headers.queryValue}"))
			.to("sql:" + consumerBase.getQueryConsulta() + "?dataSource=#onyx")
			.log(LoggingLevel.DEBUG, log, "mensajes..... ${body}")
			.end();
	}
}
