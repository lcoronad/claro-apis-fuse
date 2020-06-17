package com.claro.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Response DTO Object Consulta Factura MIN")
@JsonPropertyOrder({"data","respuesta"})
@JsonIgnoreProperties({"usuario",""})
public class ResponseMin extends Response {

	private static final long serialVersionUID = -2497950466361558342L;
	
	
	
	public ResponseMin() {
		super();
	}
	
	@JsonProperty("data")
	private DataResponse dataResponse = new DataResponse();

	public DataResponse getDataResponse() {
		return dataResponse;
	}

	public void setDataResponse(DataResponse dataResponse) {
		this.dataResponse = dataResponse;
	}

	
	

}
