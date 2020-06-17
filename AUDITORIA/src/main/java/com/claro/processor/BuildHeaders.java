package com.claro.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.claro.dto.Audit;

public class BuildHeaders implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		Audit audit = exchange.getIn().getBody(Audit.class);
		
		exchange.getIn().setHeader("api", audit.api);
		exchange.getIn().setHeader("fechaTransaccion", audit.fechaTransaccion);
		exchange.getIn().setHeader("min_code", audit.request.data.numeroCelular);
		exchange.getIn().setHeader("cliente", audit.request.data.cliente);
		exchange.getIn().setHeader("ciudad", audit.response.data.ciudad);
		exchange.getIn().setHeader("pais", audit.response.data.pais);
		exchange.getIn().setHeader("dpto", audit.response.data.dpto);
		exchange.getIn().setHeader("codigoRespuesta", audit.response.respuesta.codigoRespuesta);
		exchange.getIn().setHeader("request", audit.request.toString());
		exchange.getIn().setHeader("response", audit.response.toString());
		exchange.getIn().setHeader("logUUIDTxr", audit.logUUIDTxr);
		exchange.getIn().setHeader("logUsuario", audit.logUsuario);
		exchange.getIn().setHeader("recursoApp", audit.recursoApp);
		exchange.getIn().setHeader("detalleR", audit.detalleR);
		exchange.getIn().setHeader("idCliente", audit.idCliente);
		exchange.getIn().setHeader("fechaU", audit.response.data.fechaUbicacion);
		exchange.getIn().setHeader("latitudLbs", audit.latitudLbs);
		exchange.getIn().setHeader("longitudLbs", audit.longitudLbs);
		exchange.getIn().setHeader("statusLBS", audit.statusLBS);
		exchange.getIn().setHeader("requestLBS", audit.requestLBS);
		exchange.getIn().setHeader("responseLBS", audit.responseLBS);
		exchange.getIn().setHeader("statusPIC", audit.statusPIC);
		exchange.getIn().setHeader("responsePIC", audit.responsePIC);
	}

}
