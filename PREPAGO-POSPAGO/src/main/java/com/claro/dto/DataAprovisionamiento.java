package com.claro.dto;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotBlank;

import com.claro.utils.ValidatorEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@XmlRootElement
@JsonAutoDetect
public class DataAprovisionamiento {

	@NotBlank(message = "{param.notblank}")
	@Pattern(regexp = "^[0-9]*$", message = "{param.special.character.number}")
	@ValidatorEnum(dataCanales = { 1, 2, 3 }, message = "{param.canal}")
	public String canal = "";

	@NotBlank(message = "{param.notblank}")
	@Pattern.List({ @Pattern(regexp = "^(\\s*|\\d{10,10})$", message = "{param.sizemin}"),
			@Pattern(regexp = "^3.*$", message = "{param.startsthree}"),
			@Pattern(regexp = "^[0-9]*$", message = "{param.nowhitespaces}") })
	public String min;

	@NotBlank(message = "{param.notblank}")
	@Pattern.List({ @Pattern(regexp = "^(\\s*|\\d{5,6})$", message = "{param.sizeofferid}"),
			@Pattern(regexp = "^[0-9]*$", message = "{param.special.character.number}") })
	public String offerId;

	@NotBlank(message = "{param.notblank}")
	@Pattern.List({
		@Pattern(regexp = "^(\\s*|\\d{4,6})$", message = "{param.sizehostid}"),
		@Pattern(regexp = "^[0-9]*$", message = "{param.special.character.number}")
	})
	public String hostId;

	@NotBlank(message = "{param.notblank}")
	public String nombrePaquete;

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

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

	public String getNombrePaquete() {
		return nombrePaquete;
	}

	public void setNombrePaquete(String nombrePaquete) {
		this.nombrePaquete = nombrePaquete;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

}
