package com.claro.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Assert Solutions S.A.S
 *
 */
@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@ApiModel(description = "Response DTO Object Consulta Factura")
public class Response implements Serializable {

	private static final long serialVersionUID = -6104876573750302537L;

	

	@JsonProperty("respuesta")
	private DetalleResponse detalleResponse = new DetalleResponse();

	public Response() {

	}

	public Response(DetalleResponse detalleResponse) {
		
		this.detalleResponse = detalleResponse;
	}

	

	public DetalleResponse getDetalleResponse() {
		return detalleResponse;
	}

	public void setDetalleResponse(DetalleResponse detalleResponse) {
		this.detalleResponse = detalleResponse;
	}
	
	
	
	

}
