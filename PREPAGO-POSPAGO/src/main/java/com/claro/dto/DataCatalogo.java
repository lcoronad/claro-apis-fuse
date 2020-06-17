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
public class DataCatalogo {

	@NotBlank(message = "{param.notblank}")
	@Pattern(regexp = "^[0-9]*$", message = "{param.special.character.number}")
	@ValidatorEnum(dataCanales = { 1, 2, 3 }, message = "{param.canal}")
	public String canal = "";
	
	@NotBlank(message = "{param.notblank}")
	@Size(min = 10, max = 10, message = "{param.size}")
	@Pattern.List({
		@Pattern(regexp = "^[0-9]*$", message = "{param.special.character.number}"),
		@Pattern(regexp = "^3.*$", message = "{param.startsthree}")
		
	})
	public String min = "";

	public String getCanal() {
		return canal;
	}
	
	public String getMin() {
		return this.min;
	}

}
