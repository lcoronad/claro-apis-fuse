package com.claro.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;



@JsonSerialize
@XmlRootElement
@JsonAutoDetect
public class Auditoria {
	
	@JsonProperty
	public String api = " ";
	@JsonProperty
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Bogota")
	public String fechaTransaccion;

	@JsonProperty
	public String logUUIDTxr;

	@JsonProperty
	public String logUsuario = " ";

	@JsonProperty
	public String recursoApp;

	@JsonProperty
	public String detalleR;

	@JsonProperty
	public String idCliente;

	@JsonProperty
	public String nombreCliente;
	
	@JsonProperty
	public String codResponse;
	
	@JsonProperty
	public String codResponseSE = "";
	
	@JsonProperty
	public String msmRequestSE = "";
	
	@JsonProperty
	public String msmResponseSE = "";
	
	@JsonProperty
	public String nameSE = "";
	
	@JsonProperty
	public String canal = "";
	
	@JsonProperty
	public String nombreCanal = "";
	
	@JsonProperty
	public String estadoTransaccion = "";
	
	@JsonProperty
	public String productoTipo = "";
	
	@JsonProperty
	public String productoNombre = "";
	
	@JsonProperty
	public String codigoMin = "";
	
	@JsonProperty 
	public String tipoProducto = "";
	
	@JsonProperty(value = "request")
	public JsonNode request = JsonNodeFactory.instance.objectNode();
	
	@JsonProperty(value = "response")
	public JsonNode response = JsonNodeFactory.instance.objectNode();

}
