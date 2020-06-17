package com.claro.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Response DTO Object Consulta Catalogo")
@JsonPropertyOrder({"data","respuesta"})
public class ResponseCatalogo extends Response {

	
	private static final long serialVersionUID = -2529222765261413288L;
	
	@JsonProperty("data")
	public transient List<JsonNode> data = new ArrayList<>();

}
