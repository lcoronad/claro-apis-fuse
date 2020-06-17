package com.claro.esb.authentication.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockRoute extends RouteBuilder {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	public static final String ROUTE_CUSTOMER = "direct:customer";
	
	
	@Override
	public void configure() throws Exception {
		
		from(ROUTE_CUSTOMER).routeId("ROUTE_CUSTOMER").streamCaching("true")
			.log(LoggingLevel.INFO, logger, "Inicio la ruta de customer")
			.to("velocity:listcustomer.js")
			.log(LoggingLevel.INFO, logger, "finalizo ok")
			.end();
		
	}

}
