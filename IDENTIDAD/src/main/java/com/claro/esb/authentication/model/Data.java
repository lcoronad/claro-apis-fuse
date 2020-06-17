package com.claro.esb.authentication.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class Data {

	@Email(message = "Digitó un correo no válido")
	private String usuario;

	private String contrasena;

	@Size(min = 10, max = 10, message="El número celular debe ser de 10 dígitos")
	private String min;

	@Min(value = 1, message = "Se debe seleccionar solo una opción de 1 a 4")
	@Max(value = 4, message = "Se debe seleccionar solo una opción de 1 a 4")
	private String tipoIdentificacion;

	private String numeroIdentificacion;

	@Pattern(regexp = "^[0-9]*$", message = "Número de cuenta no válido")
	@Size(min = 5, max = 20, message = "Número de cuenta no válido")
	private String cuenta;

	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public String getNumeroIdentificacion() {
		return numeroIdentificacion;
	}

	public void setNumeroIdentificacion(String numeroIdentificacion) {
		this.numeroIdentificacion = numeroIdentificacion;
	}

	@Override
	public String toString() {
		return "Data [usuario=" + usuario + ", contrasena=" + contrasena + ", min=" + min + ", tipoIdentificacion="
				+ tipoIdentificacion + ", numeroIdentificacion=" + numeroIdentificacion + ", cuenta=" + cuenta + "]";
	}

}
