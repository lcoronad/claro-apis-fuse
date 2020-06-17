package com.claro.dto;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class AuditRappi {

	@JsonProperty(value = "idCliente")
	public String idCliente;
	
	@JsonProperty(value = "request")
	public Map<?, ?> request = new HashMap<>();
	
	@JsonProperty(value = "codResponse")
	public String codRespuesta;
	
	@JsonProperty(value = "detalleR")
	public String detalleRespuesta;
	
	@JsonProperty(value = "response")
	public Map<?, ?> response = new HashMap<>();
	
	@JsonProperty(value = "api")
	@ApiModelProperty(dataType = "String")
	public String api;
	
	@JsonProperty(value = "recursoApp")
	public String recurso;
	
	@JsonProperty(value = "nombreCliente")
	public String nombreCliente;
	
	@JsonProperty(value = "logUUIDTxr")
	public String logUUIDTxr;
	
	@JsonProperty(value = "logUsuario")
	public String logUsuario;
	
	@JsonProperty(value = "fechaTransaccion")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Bogota")
	public String fechaTransaccion;
	
	
	@JsonProperty(value = "canal")
	public String canalContacto;
	
	@JsonProperty(value = "nombreCanal")
	public String canalContactoNombre;
	
	@JsonProperty(value = "codigoMin")
	public String codigoMin;
	
	@JsonProperty(value = "estadoTransaccion")
	public String transaccionEstado;
	
	@JsonProperty(value = "productoTipo")
	public String paqueteTipoProducto;
	
	@JsonProperty(value = "productoNombre")
	public String paqueteNombreProducto;
	
	@JsonProperty(value = "codResponseSE")
	public String codigoRespuesta;
	
	@JsonProperty(value = "msmResponseSE")
	public String mensajeRespuesta;
	
	@JsonProperty(value = "msmRequestSE")
	public String requestServicio;
	
	@JsonProperty(value = "nameSE")
	public String nombreServicio;
	
	@JsonProperty
	public String tipoProducto;
}
