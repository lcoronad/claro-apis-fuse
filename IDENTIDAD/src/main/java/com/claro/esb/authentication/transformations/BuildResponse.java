package com.claro.esb.authentication.transformations;

import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Component;

@Component("buildResponse")
public class BuildResponse implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
		if (exception != null) {
			if (exception instanceof BeanValidationException) {
				BeanValidationException exc = (BeanValidationException) exception;
				exc.getConstraintViolations().stream().map(c -> {
					exchange.getIn().setHeader("mensajeRespuesta", c.getMessage().toString());
					return null;
				}).collect(Collectors.toList());
			} else if(exception instanceof UncategorizedSQLException) {
				UncategorizedSQLException ex = (UncategorizedSQLException) exception;
				if(ex.getLocalizedMessage().contains("No Encontrado")) {
					exchange.getIn().setHeader("codigoRespuesta", "OK");
					exchange.getIn().setHeader("mensajeRespuesta", "Cliente no encontrado");
				}
			}else {
				if (exception.getCause() != null) {
					String causa = exception.getCause().toString();
					exchange.getIn().setHeader("mensajeRespuesta", "Se ha producido un error en la autenticación");
				}
			}
		}
	}
}