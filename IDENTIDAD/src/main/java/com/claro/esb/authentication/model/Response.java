package com.claro.esb.authentication.model;

public class Response {

	private Respuesta respuesta;

	public Respuesta getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(Respuesta respuesta) {
		this.respuesta = respuesta;
	}

	@Override
	public String toString() {
		return "ClassPojo [respuesta = " + respuesta + "]";
	}
}
