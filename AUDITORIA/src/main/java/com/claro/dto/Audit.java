package com.claro.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Assert Solutions
 *
 */
@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class Audit implements Serializable {


	private static final long serialVersionUID = 8665925013872152970L;

	@JsonProperty
	@ApiModelProperty(dataType = "String")
	public String api;
	@JsonProperty
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "America/Bogota")
	public  String fechaTransaccion;
	
	public Request request;
	public Response response;
	
	@JsonProperty
	public String logUUIDTxr;
	
	@JsonProperty 
	public String logUsuario;
	
	@JsonProperty
	public String recursoApp;
	
	@JsonProperty
	public String detalleR;
	
	@JsonProperty
	public String idCliente;
	
	@JsonProperty
	public String latitudLbs;
	
	@JsonProperty
	public String longitudLbs;
	
	@JsonProperty
	public String statusLBS;
	
	@JsonProperty
	public String requestLBS;
	
	@JsonProperty
	public String responseLBS;
	
	@JsonProperty
	public String statusPIC;
	
	@JsonProperty
	public String responsePIC;

	
	@XmlRootElement
	@JsonAutoDetect
	@JsonSerialize
	@JsonIgnoreProperties
	@ApiModel(description = "Request DTO Object")
	public static class Request {
		@JsonProperty
	    public Data data;

		@Override
		public String toString() {
			return "{\"request\": {" + data + "}}";
		}
		
	}
	
	@XmlRootElement
	@JsonAutoDetect
	@JsonSerialize
	public static class Data {
	    @JsonProperty
	    @ApiModelProperty(dataType = "Integer")
	    public String numeroCelular;
	    @JsonProperty
	    @ApiModelProperty(dataType = "String")
	    public  String cliente;
		@Override
		public String toString() {
			return " \"data\" :{\"numeroCelular\":\"" + numeroCelular + "\", \"cliente\":\"" + cliente + "\"}";
		}

	    

	}
	
	@XmlRootElement
	@JsonAutoDetect
	@JsonSerialize
	@ApiModel(description = "Response DTO Object")
	public static class Response implements Serializable {

	    private static final long serialVersionUID = -6104876573750302537L;

	    public com.claro.data.Data data;
	    public Respuesta respuesta;
		@Override
		public String toString() {
			return "{\"response\": {\"data\":" + data + ", \"respuesta\":" + respuesta + "}}";
		}

	   

	}

	@XmlRootElement
	@JsonAutoDetect
	@JsonSerialize
	public static class Respuesta {
	    @JsonProperty
	    @ApiModelProperty(dataType = "String")
	    public String codigoRespuesta;
	    @JsonProperty
	    @ApiModelProperty(dataType = "String")
	    public String mensajeRespuesta;
		@Override
		public String toString() {
			return "{\"codigoRespuesta\":\"" + codigoRespuesta + "\", \"mensajeRespuesta\":\"" + mensajeRespuesta + "\"}";
		}
	    
	    
	}

}









