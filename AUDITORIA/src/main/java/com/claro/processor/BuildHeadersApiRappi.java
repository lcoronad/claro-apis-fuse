package com.claro.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.claro.dto.AuditRappi;

public class BuildHeadersApiRappi implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		AuditRappi audit = exchange.getIn().getBody(AuditRappi.class);
		exchange.getIn().setHeader("idCliente", audit.idCliente);
		exchange.getIn().setHeader("httpRequest", audit.request);
		exchange.getIn().setHeader("codRespuesta", audit.codRespuesta);
		exchange.getIn().setHeader("detalleRespuesta", audit.detalleRespuesta);
		exchange.getIn().setHeader("HttpResponse", audit.response);
		exchange.getIn().setHeader("api", audit.api);
		exchange.getIn().setHeader("recurso", audit.recurso);
		exchange.getIn().setHeader("nombreCliente", audit.nombreCliente);
		exchange.getIn().setHeader("logUsuario", audit.logUsuario);
		exchange.getIn().setHeader("logUUIDTxr", audit.logUUIDTxr);
		exchange.getIn().setHeader("fechaTransaccion", audit.fechaTransaccion);
		
		exchange.getIn().setHeader("canalContacto", audit.canalContacto);
		exchange.getIn().setHeader("canalContactoNombre", audit.canalContactoNombre);
		exchange.getIn().setHeader("codigoMin", audit.codigoMin);
		exchange.getIn().setHeader("transaccionEstado", audit.transaccionEstado);
		exchange.getIn().setHeader("paqueteTipoProducto", audit.paqueteTipoProducto);
		exchange.getIn().setHeader("paqueteNombreProducto", audit.paqueteNombreProducto);
		exchange.getIn().setHeader("codigoRespuesta", audit.codigoRespuesta);
		exchange.getIn().setHeader("mensajeRespuesta", audit.mensajeRespuesta);
		exchange.getIn().setHeader("requestServicio", audit.requestServicio);
		exchange.getIn().setHeader("nombreServicio", audit.nombreServicio);
		exchange.getIn().setHeader("tipoProducto", audit.tipoProducto);
	}

}
