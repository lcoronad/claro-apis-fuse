package com.claro.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class DetalleError {
	
	@JsonProperty
	public String campo = "";
	@JsonProperty
	public String mensaje ="";
	@JsonProperty
	public String valor ="";

}
