package com.claro.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Response DTO Object Aplicar Pago")
@JsonPropertyOrder({ "data", "respuesta" })
public class ResponseAplicarCargo extends Response {

	private static final long serialVersionUID = 1L;

	public ResponseAplicarCargo() {
		super();
	}

//	@JsonProperty("data")
//	private DataResponseAP dataResponse;

	@JsonProperty("data")
	private transient JsonNode detalles = JsonNodeFactory.instance.objectNode();

	public JsonNode getDetalles() {
		return detalles;
	}

	public void setDetalles(JsonNode detalles) {
		this.detalles = detalles;
	}

//	public DataResponseAP getDataResponse() {
//		return dataResponse;
//	}
//
//	public void setDataResponse(DataResponseAP dataResponse) {
//		this.dataResponse = dataResponse;
//	}

}
