package com.claro.dto;

import java.io.Serializable;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Request DTO Consulta Factura")
public class Request implements Serializable {

	private static final long serialVersionUID = -4304150100793083547L;

	@Valid
//	@ValidParameters(message = "{param.validationreference}")
	private  Data data = new Data();

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
	
	

	

}
