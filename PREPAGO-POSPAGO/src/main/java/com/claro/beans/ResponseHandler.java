	package com.claro.beans;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.claro.dto.DataResponse;
import com.claro.dto.DataResponseAP;
import com.claro.dto.DataResponseItel;
import com.claro.dto.DetalleError;
import com.claro.dto.DetalleResponse;
import com.claro.dto.Response;
import com.claro.dto.ResponseAplicarCargo;
import com.claro.dto.ResponseAprovisionamiento;
import com.claro.dto.ResponseCatalogo;
import com.claro.dto.ResponseMP;
import com.claro.dto.ResponseMin;
import com.claro.utils.ConstantUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.ApiModelProperty;

@Component
public class ResponseHandler {

	@Autowired
	private Environment env;
	
	private Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
	
	private static final String MESSAGE_FAILED = "message.codigo.failed";
	private static final String CODIGO_OK = "message.codigo.ok";
	private static final String MESSAGE_OK = "message.detalle.ok";

	@Handler
	@ApiModelProperty(notes = "Parametro De Salida")
	public ResponseMin handler(Exchange exchange) throws Exception {
		DetalleResponse detalleResponse = new DetalleResponse();
		detalleResponse.codigo = env.getProperty(CODIGO_OK);
		detalleResponse.mensajeRespuesta = env.getProperty(MESSAGE_OK);

		ObjectMapper objectMapper = new ObjectMapper();
		DataResponse dataResponse = objectMapper.readValue(exchange.getIn().getBody(String.class).getBytes(StandardCharsets.UTF_8), DataResponse.class);
		if(dataResponse.factura.size() != 0) {
			ObjectNode object = objectMapper.valueToTree(dataResponse.factura.get(0));
			object.remove("nombreUsuario");
			object.fieldNames().forEachRemaining(x -> {
				logger.info("Key: {}",x);
				if(x.startsWith("direcci")) {
					object.remove(x);
				}
				logger.info("Key: {}",x);
				});
			dataResponse.factura = object;
		}
		ResponseMin responseMin = new ResponseMin();
		responseMin.setDetalleResponse(detalleResponse);
		responseMin.setDataResponse(dataResponse);
		return responseMin;
	}

	public Response errorValidatorBeans(String codigo, String mensaje, List<DetalleError> listDetalles) {

		Response response = new Response();
		DetalleResponse detalleResponse = new DetalleResponse();
		detalleResponse.codigo = codigo;
		detalleResponse.mensajeRespuesta = new String(env.getProperty(mensaje).getBytes(StandardCharsets.ISO_8859_1),
				StandardCharsets.UTF_8);
		if (!listDetalles.isEmpty())
			detalleResponse.listDetalleError = listDetalles;
		response.setDetalleResponse(detalleResponse);
		Response responseMP = new ResponseMP();
		responseMP.setDetalleResponse(detalleResponse);
		return responseMP;
	}

	public ResponseMP responseErrorMP() {
		DetalleResponse detalleResponse = new DetalleResponse();
		detalleResponse.codigo = env.getProperty(MESSAGE_FAILED);
		detalleResponse.mensajeRespuesta = env.getProperty("message.detalle.failed");
		ResponseMP responseMP = new ResponseMP();
		responseMP.setDetalleResponse(detalleResponse);
		return responseMP;
	}

	public ResponseMP responseMP(String codigo, String message, String data) throws IOException {
		DetalleResponse detalleResponse = new DetalleResponse();
		detalleResponse.codigo = codigo;
		detalleResponse.mensajeRespuesta = new String(env.getProperty(message).getBytes(StandardCharsets.ISO_8859_1),
				StandardCharsets.UTF_8);
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseMP responseMP = objectMapper.readValue(data, ResponseMP.class);
		responseMP.setDetalleResponse(detalleResponse);
		return responseMP;
	}

	@SuppressWarnings("unchecked")
	public ResponseCatalogo responseCatalogo(String codigo, String message, String data) throws IOException {
		DetalleResponse detalleResponse = new DetalleResponse();
		detalleResponse.codigo = codigo;
		detalleResponse.mensajeRespuesta = new String(env.getProperty(message).getBytes(StandardCharsets.ISO_8859_1),
				StandardCharsets.UTF_8);
		ObjectMapper objectMapper = new ObjectMapper();
		List<JsonNode> listProducts = objectMapper.readValue(data, List.class);
		ResponseCatalogo responseCatalogo = new ResponseCatalogo();
		responseCatalogo.data = listProducts;
		responseCatalogo.setDetalleResponse(detalleResponse);
		return responseCatalogo;
	}

	public ResponseAprovisionamiento responseErrorAprovisionamiento(String codigo, String message, String data)
			throws IOException {
		DetalleResponse detalleResponse = new DetalleResponse();
		detalleResponse.codigo = codigo;
		detalleResponse.mensajeRespuesta = new String(env.getProperty(message).getBytes(StandardCharsets.ISO_8859_1),
				StandardCharsets.UTF_8);
		ResponseAprovisionamiento responseAprov = new ResponseAprovisionamiento();
		responseAprov.setDetalleResponse(detalleResponse);
		return responseAprov;
	}

	public ResponseAprovisionamiento responseAprovisionamiento(Exchange exchange) throws Exception {
		DetalleResponse detalleResponse = new DetalleResponse();
		DataResponseAP dataResponse = new DataResponseAP();
		DataResponseItel itel = new DataResponseItel();
		itel.setCodigo(exchange.getProperty("resCodRespuesta").toString());
		itel.setDescripcion(exchange.getProperty("resDesc").toString());
		itel.setMin(exchange.getProperty(ConstantUtil.MIN_APROVISIONAMIENTO).toString());
		itel.setOfferId(exchange.getProperty(ConstantUtil.OFFERID_APROVISIONAMIENTO).toString());
		if (itel.getCodigo().equals("1")
				&& exchange.getProperty(ConstantUtil.RESPNSE_CODE_SE).toString().equals("200")) {
			// Transaction OK
			exchange.setProperty(ConstantUtil.COD_RESPONSE, env.getProperty(CODIGO_OK));
			exchange.setProperty(ConstantUtil.DETALE_RESPONSE, env.getProperty("message.detalle.aprovisionamiento.ok"));
			detalleResponse.codigo = env.getProperty(CODIGO_OK);
			detalleResponse.mensajeRespuesta = env.getProperty(MESSAGE_OK);
			exchange.setProperty(ConstantUtil.ESTADO_TRANSACCION, env.getProperty(MESSAGE_OK));
		} else {
			// Error
			exchange.setProperty(ConstantUtil.COD_RESPONSE, env.getProperty(MESSAGE_FAILED));
			exchange.setProperty(ConstantUtil.DETALE_RESPONSE,
					env.getProperty("message.detalle.aprovisionamiento.failed"));
			detalleResponse.codigo = env.getProperty(MESSAGE_FAILED);
			exchange.setProperty(ConstantUtil.ESTADO_TRANSACCION,env.getProperty(MESSAGE_FAILED));
			detalleResponse.mensajeRespuesta = env.getProperty("message.detalle.aprovisionamiento.failed");
		}

		dataResponse.setResponse(itel);
		ResponseAprovisionamiento responseAP = new ResponseAprovisionamiento();
		responseAP.setDataResponse(dataResponse);
		responseAP.setDetalleResponse(detalleResponse);
		return responseAP;
	}

	public ResponseAplicarCargo responseAplicarCargo(Exchange ex)
			throws IOException {
		String isOk = ex.getIn().getHeader("xpathTest", String.class);
		String data = ex.getIn().getHeader("data", String.class);		
		DetalleResponse detalleResponse = new DetalleResponse();
		if (isOk.equalsIgnoreCase("true")) {
			ex.setProperty(ConstantUtil.COD_RESPONSE, env.getProperty(CODIGO_OK));
			ex.setProperty(ConstantUtil.DETALE_RESPONSE, env.getProperty("message.detalle.aplicar.cargo.ok"));
			detalleResponse.codigo = env.getProperty(CODIGO_OK);
			detalleResponse.mensajeRespuesta = env.getProperty(MESSAGE_OK);
		} else {
			ex.setProperty(ConstantUtil.COD_RESPONSE, env.getProperty(MESSAGE_FAILED));
			ex.setProperty(ConstantUtil.DETALE_RESPONSE, env.getProperty("message.detalle.aplicar.cargo.failed"));
			detalleResponse.codigo = env.getProperty(MESSAGE_FAILED);
			detalleResponse.mensajeRespuesta = env.getProperty("message.detalle.failed");
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseAplicarCargo responseAR = objectMapper.readValue(data, ResponseAplicarCargo.class);
		responseAR.setDetalleResponse(detalleResponse);
		return responseAR;
	}

	public ResponseAplicarCargo responseErrorAplicarCargo(String codigo, String message, String data)
			throws IOException {
		DetalleResponse detalleResponse = new DetalleResponse();
		detalleResponse.codigo = codigo;
		detalleResponse.mensajeRespuesta = new String(env.getProperty(message).getBytes(StandardCharsets.ISO_8859_1),
				StandardCharsets.UTF_8);
		ResponseAplicarCargo responseAprov = new ResponseAplicarCargo();
		responseAprov.setDetalleResponse(detalleResponse);
		return responseAprov;
	}
}
