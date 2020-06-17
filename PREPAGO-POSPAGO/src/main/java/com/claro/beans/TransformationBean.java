package com.claro.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.claro.utils.FuncionesUtileria;

@Component
public class TransformationBean {

	@Handler
	public void setProperties(Exchange exchange) throws Exception {
		exchange.setProperty("APPaymentReference",
				FuncionesUtileria.completeIdReference((String) exchange.getProperty("APPaymentReference")));
	}
}