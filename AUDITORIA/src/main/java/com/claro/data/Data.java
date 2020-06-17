package com.claro.data;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public
class Data {
	@JsonProperty
	@ApiModelProperty(dataType = "String")
	public  String ciudad;
	@JsonProperty
	@ApiModelProperty(dataType = "String")
	public  String pais;
	@JsonProperty
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(dataType = "String")
	public  String fechaUbicacion;

	@JsonProperty
	@ApiModelProperty(dataType = "String")
	public  String dpto;

	@Override
	public String toString() {
		return " {\"ciudad\":\"" + ciudad + "\", \"pais\":\"" + pais + "\", \"fechaUbicacion\":\"" + fechaUbicacion + "\", \"dpto\":\"" + dpto
				+ "\"}";
	}
	
	

}
