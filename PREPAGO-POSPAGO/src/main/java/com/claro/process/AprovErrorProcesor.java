package com.claro.process;

import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.springframework.stereotype.Component;

@Component("buildResponse")
public class AprovErrorProcesor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		if (exception != null) {
			if (exception instanceof BeanValidationException) {
				BeanValidationException exc = (BeanValidationException) exception;
				exc.getConstraintViolations().stream().map(c -> {
					exchange.getIn().setHeader("mensajeRespuesta", c.getMessage());
					return null;
				}).collect(Collectors.toList());
			} else {
				if (exception.getCause() != null) {
					
				}
			}
		}
	}
}