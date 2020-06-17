package com.claro.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.claro.dto.Auditoria;
import com.claro.utils.ConstantUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BuildAuditoriaProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String data = objectMapper.writeValueAsString(exchange.getProperty(ConstantUtil.RESPONSE, Object.class));
		JsonNode jsonData = objectMapper.readValue(data, JsonNode.class);
		JsonNode request = objectMapper.readValue(exchange.getProperty(ConstantUtil.REQUEST, String.class),
				JsonNode.class);
		Auditoria auditoria = exchange.getProperty(ConstantUtil.AUDITORIA_PROPERTY, Auditoria.class);
		auditoria.api = exchange.getProperty(ConstantUtil.API_NAME, String.class);
		auditoria.codResponseSE = exchange.getProperty(ConstantUtil.RESPNSE_CODE_SE, String.class) == null ? ""
				: exchange.getProperty(ConstantUtil.RESPNSE_CODE_SE, String.class);
		auditoria.msmRequestSE = exchange.getProperty(ConstantUtil.REQUEST_SE, String.class) == null ? ""
				: exchange.getProperty(ConstantUtil.REQUEST_SE, String.class);
		
		auditoria.msmResponseSE = exchange.getProperty(ConstantUtil.RESPONSE_SE, String.class) == null ? ""	: exchange.getProperty(ConstantUtil.RESPONSE_SE,String.class);
		auditoria.nameSE = exchange.getProperty(ConstantUtil.NOMBRE_SE, String.class);
		auditoria.codResponse = exchange.getProperty(ConstantUtil.COD_RESPONSE, String.class);
		auditoria.detalleR = exchange.getProperty(ConstantUtil.DETALE_RESPONSE, String.class);
		auditoria.request = request;
		auditoria.response = jsonData;
		auditoria.canal = exchange.getProperty(ConstantUtil.CANAL, String.class);
		auditoria.nombreCanal = exchange.getProperty(ConstantUtil.NOMBRE_CANAL, String.class);
		auditoria.logUsuario = exchange.getProperty(ConstantUtil.LOG_USUARIO, String.class);
		auditoria.estadoTransaccion = exchange.getProperty(ConstantUtil.ESTADO_TRANSACCION, String.class) == null ? ""
				: exchange.getProperty(ConstantUtil.ESTADO_TRANSACCION, String.class);
		loadAprovisionamientoData(exchange);
		exchange.setProperty(ConstantUtil.AUDITORIA_PROPERTY, auditoria);
	}

	private void loadAprovisionamientoData(Exchange ex) {
		Auditoria audit = ex.getProperty(ConstantUtil.AUDITORIA_PROPERTY, Auditoria.class);
		audit.productoTipo = ex.getProperty(ConstantUtil.OFFERID_APROVISIONAMIENTO, String.class) == null ? ""
				: ex.getProperty(ConstantUtil.OFFERID_APROVISIONAMIENTO, String.class);
		audit.productoNombre = ex.getProperty(ConstantUtil.OFFERID_NOMBRE, String.class) == null ? ""
				: ex.getProperty(ConstantUtil.OFFERID_NOMBRE, String.class);
		audit.codigoMin = ex.getProperty(ConstantUtil.MIN_APROVISIONAMIENTO, String.class) == null ? ""
				: ex.getProperty(ConstantUtil.MIN_APROVISIONAMIENTO, String.class);
	}

}
