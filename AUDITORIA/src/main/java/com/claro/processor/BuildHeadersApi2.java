package com.claro.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.claro.dto.AuditApi2;

public class BuildHeadersApi2 implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		AuditApi2 audit = exchange.getIn().getBody(AuditApi2.class);
		exchange.getIn().setHeader("api", audit.api);
		exchange.getIn().setHeader("fechaTransaccion", audit.fechaTransaccion);
		exchange.getIn().setHeader("min_code", audit.minCode);
		exchange.getIn().setHeader("nombreCliente", audit.nombreCliente);
		exchange.getIn().setHeader("codigoRespuesta", audit.codResponse);
		exchange.getIn().setHeader("request", audit.request);
		exchange.getIn().setHeader("response", audit.response);
		exchange.getIn().setHeader("logUUIDTxr", audit.logUUIDTxr);
		exchange.getIn().setHeader("logUsuario", audit.logUsuario);
		exchange.getIn().setHeader("recursoApp", audit.recursoApp);
		exchange.getIn().setHeader("detalleR", audit.detalleR);
		exchange.getIn().setHeader("idCliente", audit.idCliente);
		exchange.getIn().setHeader("tipoDoc", audit.tipoDoc);
		exchange.getIn().setHeader("numeroDoc", audit.numeroDoc);
		exchange.getIn().setHeader("cuenta", audit.cuenta);
		exchange.getIn().setHeader("usuario", audit.usuario);
	
	}

}
