package com.claro.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class DetalleResponse implements Serializable{
	
	
	private static final long serialVersionUID = 4288036915357400724L;

	@JsonProperty
    @ApiModelProperty(dataType = "String")
    public String codigo;
	
	@JsonProperty
    @ApiModelProperty(dataType = "String")
    public String mensajeRespuesta;
	
	@JsonProperty("detalles")
	public transient List<DetalleError> listDetalleError = new ArrayList<>();
}
