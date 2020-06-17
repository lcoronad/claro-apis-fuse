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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.claro.esb.authentication.configurator.ConfigurationRoute;
import com.claro.esb.authentication.model.Request;
import com.claro.esb.authentication.model.Response;
import com.claro.esb.authentication.properties.RestExposed;

@Component
public class RestConsumerRoute extends ConfigurationRoute {
	
	@Autowired
	private RestExposed restConfig;
	
	@Autowired
	private Environment env;

	@Override
	public void configure() throws Exception {
		super.configure();
		restConfiguration()
			.component("servlet")
			.apiContextPath(restConfig.getApiPath())
			.apiProperty("api.title", restConfig.getApiTitle())
			.apiProperty("api.version", restConfig.getApiVersion())
			.apiProperty("base.path", restConfig.getApiBasePath())
			.apiProperty("cors", "true");

		rest(restConfig.getServiceName()).id(restConfig.getServiceNameId())
			.post().description(restConfig.getServiceDescription())
				.type(Request.class).description(restConfig.getServiceDescription())	
				.outType(Response.class)
				.to("direct:transformationRoute");
		
		rest(restConfig.getHealthcheckServiceName())
			.get().description("")
				.to("direct:healthcheckResponse");
		rest()
			.produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
			.consumes(MediaType.APPLICATION_JSON_UTF8_VALUE)
			.get(env.getProperty("endpoint.customer.listcustomer")).outType(String.class)
			.to(MockRoute.ROUTE_CUSTOMER);
	
	}
}
