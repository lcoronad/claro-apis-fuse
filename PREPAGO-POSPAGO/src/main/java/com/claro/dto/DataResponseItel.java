package com.claro.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class DataResponseItel {

	@JsonProperty
	public String min = "";
	@JsonProperty
	public String offerId = "";
	@JsonProperty
	public String codigo = "";
	@JsonProperty
	public String descripcion = "";

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "DataResponseItel [min=" + min + ", offerId=" + offerId + ", codigo=" + codigo + ", descripcion="
				+ descripcion + "]";
	}

}
