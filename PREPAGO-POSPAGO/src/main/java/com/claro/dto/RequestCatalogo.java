package com.claro.dto;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Request DTO Consulta Catálogo")
public class RequestCatalogo {
	
	@JsonProperty("data")
	@Valid
	public DataCatalogo data = new DataCatalogo();

}
