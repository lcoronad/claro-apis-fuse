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
@ApiModel(description = "Response DTO Object Consulta Factura REFERENCIA")
@JsonPropertyOrder({"data","respuesta"})
public class ResponseMP extends Response{

	private static final long serialVersionUID = -7983950885435905187L;
	
	public ResponseMP() {
		super();
	}
	
	@JsonProperty("data")
	private transient JsonNode detalles = JsonNodeFactory.instance.objectNode();
	
	

	public JsonNode getDetalles() {
		return detalles;
	}

	public void setDetalles(JsonNode detalles) {
		this.detalles = detalles;
	}
	
	

}
