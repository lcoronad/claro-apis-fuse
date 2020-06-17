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
@ApiModel(description = "Request DTO Aprovisionamiento")
public class RequestAplicarCargo {

	@JsonProperty("data")
	@Valid
	public DataAplicarCargo data = new DataAplicarCargo();

	public DataAplicarCargo getData() {
		return data;
	}

	public void setData(DataAplicarCargo data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "RequestAplicarCargo data=" + data;
	}

}
