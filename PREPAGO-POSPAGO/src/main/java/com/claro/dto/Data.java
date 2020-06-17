package com.claro.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotBlank;

import com.claro.utils.ValidatorEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@XmlRootElement
@JsonAutoDetect
public  class Data implements Serializable {
	

	

	private static final long serialVersionUID = 6196113960476440075L;

	@JsonProperty
	@NotBlank(message = "{param.notblank}")
	@Pattern(regexp = "^[0-9]*$", message = "{param.special.character.number}")
	@ValidatorEnum(dataCanales = {1,2,3}, message = "{param.canal}")
	public String canal;

	@JsonProperty
	@Pattern.List({
		@Pattern(regexp = "^[0-9]*$", message = "{param.special.character.number}"),
		@Pattern(regexp = "^3.*$", message = "{param.startsthree}"),
		@Pattern(regexp = ".{10,10}" , message = "{param.size}")
	})
	public String min = "";
	
//	@JsonProperty
//	@Size(max = 50, min = 0, message = "{param.max50}")
	@JsonInclude(Include.NON_EMPTY)
	public String bankCode = "";
	
	@Pattern(regexp = "^(\\s*|\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2})$", message = "{param.invalid.date.format}")
	@JsonInclude(Include.NON_EMPTY)
	public String paymentReceptionDate = "";
	
	
//	@JsonProperty
//	@Pattern(regexp = "^(\\s*|\\d{8,10})$", message = "{param.sizereference}")
	@JsonInclude(Include.NON_EMPTY)
	public String idReferencia = "";

	@Override
	public String toString() {
		return "Data min=" + min + ", idReferencia=" + idReferencia + "";
	}
	
	public String getCanal() {
		return canal;
	}

	public String getMin() {
		return min;
	}

	public String getIdReferencia() {
		return idReferencia;
	}

}
