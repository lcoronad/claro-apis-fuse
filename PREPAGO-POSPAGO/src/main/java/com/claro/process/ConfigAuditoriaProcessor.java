package com.claro.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.claro.dto.Auditoria;
import com.claro.utils.ConstantUtil;
import com.claro.utils.FuncionesUtileria;


public class ConfigAuditoriaProcessor implements Processor{
	
	
	
	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.setProperty(ConstantUtil.PROCESO_ID, exchange.getExchangeId());
		Auditoria auditoria = new Auditoria();
		auditoria.idCliente = exchange.getIn().getHeader(ConstantUtil.ID_CLIENTE,String.class);
		auditoria.nombreCliente = exchange.getIn().getHeader(ConstantUtil.NOMBRE_CLIENTE,String.class);
		auditoria.fechaTransaccion = FuncionesUtileria.fechaT();
		auditoria.logUUIDTxr = exchange.getProperty(ConstantUtil.PROCESO_ID, String.class);
		auditoria.recursoApp = exchange.getIn().getHeader(ConstantUtil.ENDPOINT, String.class);
		auditoria.tipoProducto = exchange.getProperty(ConstantUtil.TIPO_PRODUCTO, String.class);
		exchange.setProperty(ConstantUtil.REQUEST, exchange.getIn().getBody());
		exchange.setProperty(ConstantUtil.AUDITORIA_PROPERTY, auditoria);
	}

}
