package com.claro.dto;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Assert Solutions S.A
 *
 */

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class AuditApi2 {

	@JsonProperty
	@ApiModelProperty(dataType = "String")
	public String api;
	@JsonProperty
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Bogota")
	public String fechaTransaccion;

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
	public String nombreCliente;

	@JsonProperty
	public String codResponse;

	@JsonProperty
	public String usuario;

	@JsonProperty(value = "min_code")
	public String minCode;

	@JsonProperty(value = "tipo_doc")
	public String tipoDoc;

	@JsonProperty(value = "numero_doc")
	public String numeroDoc;

	@JsonProperty(value = "cuenta")
	public String cuenta;

	@JsonProperty(value = "request")
	public Map<?, ?> request = new HashMap<>();

	@JsonProperty(value = "response")
	public Map<?, ?> response = new HashMap<>();

}
