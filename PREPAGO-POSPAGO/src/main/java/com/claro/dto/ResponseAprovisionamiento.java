package com.claro.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Response DTO Object Aprovisionamiento	")
@JsonPropertyOrder({ "data", "respuesta" })
public class ResponseAprovisionamiento extends Response {

	private static final long serialVersionUID = 1L;

	public ResponseAprovisionamiento() {
		super();
	}

	@JsonProperty("data")
	private DataResponseAP dataResponse;

	public DataResponseAP getDataResponse() {
		return dataResponse;
	}

	public void setDataResponse(DataResponseAP dataResponse) {
		this.dataResponse = dataResponse;
	}

}
