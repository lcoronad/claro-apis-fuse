package com.claro.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotBlank;

import com.claro.utils.ValidatorEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@XmlRootElement
@JsonAutoDetect
public class DataAplicarCargo {

	@NotBlank(message = "{param.notblank}")
	@Size(max = 50, min = 0, message = "{param.max50}")
	@ValidatorEnum(dataCanales = { 1, 2, 3 }, message = "{param.canal}")
	public String canal = "";

	@NotBlank(message = "{param.notblank}")
//	@Pattern(regexp = "^[0-9]*$", message = "{param.special.character.number}")
	@Size(max = 50, min = 0, message = "{param.max50}")
	public String bankCode;

	@NotBlank(message = "{param.notblank}")
	@Pattern(regexp = "^[0-9.,]*?$", message = "{param.special.character.number}")
	@Size(max = 50, min = 0, message = "{param.max20}")
	public String valor;

	@NotBlank(message = "{param.notblank}")
	@Size(max = 50, min = 0, message = "{param.max50}")
	public String idReferencia;

	@NotBlank(message = "{param.notblank}")
	@Size(max = 50, min = 0, message = "{param.max50}")
	public String bankPaymentID;

	@NotBlank(message = "{param.notblank}")
	@Size(max = 3, min = 0, message = "{param.max3}")
	@ValidatorEnum(dataCanales = { 1, 2, 3 }, message = "{param.paymentmethod}")
	public String paymentMethod;

	@NotBlank(message = "{param.notblank}")
	@Pattern(regexp = "^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}$", message = "{param.invalid.date.format}")
	public String paymentAccountingDate;

	@NotBlank(message = "{param.notblank}")
	@Pattern(regexp = "^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}$", message = "{param.invalid.date.format}")
	public String paymentReceptionDate;

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getIdReferencia() {
		return idReferencia;
	}

	public void setIdReferencia(String idReferencia) {
		this.idReferencia = idReferencia;
	}

	public String getBankPaymentId() {
		return bankPaymentID;
	}

	public void setBankPaymentId(String bankPaymentId) {
		this.bankPaymentID = bankPaymentId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
