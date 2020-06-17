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
import com.claro.esb.authentication.model.AuditModel;
import com.claro.esb.authentication.properties.AmqAudit;

@Component
public class AMQAuditRoute extends ConfigurationRoute {
	private Logger log = LoggerFactory.getLogger(AMQAuditRoute.class);

	@Autowired
	private AmqAudit amqpProducerConfig;

	public void configure() throws Exception {
		super.configure();
		
		JacksonDataFormat format = new JacksonDataFormat(AuditModel.class);
		
		from("seda:amqAuditRoute").routeId("amq_audit_producer")
			.onException(Exception.class)
			.handled(true)
			.log(LoggingLevel.ERROR, log, "Error en la conexion con el AMQ: " + exceptionMessage())
			.end()
			.log(LoggingLevel.INFO, log, "Audit route")
			.setHeader("oldBody", simple("${body}"))
			.setBody(simple("${headers.auditData}"))
			.marshal(format)
			.convertBodyTo(String.class)
			.inOnly("activemqnotification:" + amqpProducerConfig.getQueue())
			.setBody(simple("${headers.oldBody}"))
			.removeHeader("oldBody")
			.log(LoggingLevel.INFO, log, "Sending to amq audit")
		.end();
	}
}
