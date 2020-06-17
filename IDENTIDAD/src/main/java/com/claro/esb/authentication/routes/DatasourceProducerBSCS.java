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

import java.net.ConnectException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.claro.esb.authentication.configurator.ConfigurationRoute;
import com.claro.esb.authentication.model.Response;
import com.claro.esb.authentication.properties.DatasourceBSCS;

@Component
public class DatasourceProducerBSCS extends ConfigurationRoute {
	private Logger log = LoggerFactory.getLogger(DatasourceProducerBSCS.class);

	@Autowired
	private DatasourceBSCS consumerBase;
	
	
	public void configure() throws Exception{
		super.configure();
		JacksonDataFormat response = new JacksonDataFormat(Response.class);
		
		onException(ConnectException.class)
		.handled(true)
		.log(LoggingLevel.ERROR, log, "Error obteniendo la conexion con la base de datos: " + exceptionMessage())
		.setHeader("CamelHttpResponseCode", simple("400"))
        .setHeader("codigoRespuesta", constant("FAILED"))
        .setHeader("mensajeRespuesta", constant("Se ha producido un error en la autenticación"))
        .bean("transformations", "buildResponse")
        .marshal(response)
		.end();
		
		from("direct:BSCSDatabaseRoute").routeId("authentication_database_bscs")
			.log(LoggingLevel.INFO, log, "on direct:BSCSDatabaseRoute ${body.data.min}")
			.setHeader("min", simple("${body.data.min}"))
			.to("sql-stored:" + consumerBase.getQueryConsulta() + "?dataSource=getConfigBSCS")
			.log(LoggingLevel.DEBUG, log, "mensajes..... ${body}")
			.end();
	}
}
