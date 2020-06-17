package com.claro.esb.authentication.model;

public class AuditModel {
	private String api; // Nombre de api,
	private String fechaTransaccion; // fecha transacción
	private String logUUIDTxr; // exchangeId del flujo,
	private String logUsuario; // valor parametrizado, es system pero puede cambiar,
	private String recursoApp; // recurso expuesto,
	private String detalleR; // mensaje de respuesta,
	private String idCliente; // id cliente 3scale,
	private String nombreCliente; // nombre cliente 3scale,
	private String codResponse; // código de respuesta,
	private String usuario; // usuario de auth,
	private String min_code; // número,
	private String tipo_doc; // tipo documento,
	private String numero_doc; // cedula,
	private String cuenta; // dato del request,
	private Request request;
	private Respuesta response;

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getFechaTransaccion() {
		return fechaTransaccion;
	}

	public void setFechaTransaccion(String fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}

	public String getLogUUIDTxr() {
		return logUUIDTxr;
	}

	public void setLogUUIDTxr(String logUUIDTxr) {
		this.logUUIDTxr = logUUIDTxr;
	}

	public String getLogUsuario() {
		return logUsuario;
	}

	public void setLogUsuario(String logUsuario) {
		this.logUsuario = logUsuario;
	}

	public String getRecursoApp() {
		return recursoApp;
	}

	public void setRecursoApp(String recursoApp) {
		this.recursoApp = recursoApp;
	}

	public String getDetalleR() {
		return detalleR;
	}

	public void setDetalleR(String detalleR) {
		this.detalleR = detalleR;
	}

	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public String getCodResponse() {
		return codResponse;
	}

	public void setCodResponse(String codResponse) {
		this.codResponse = codResponse;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getMin_code() {
		return min_code;
	}

	public void setMin_code(String min_code) {
		this.min_code = min_code;
	}

	public String getTipo_doc() {
		return tipo_doc;
	}

	public void setTipo_doc(String tipo_doc) {
		this.tipo_doc = tipo_doc;
	}

	public String getNumero_doc() {
		return numero_doc;
	}

	public void setNumero_doc(String numero_doc) {
		this.numero_doc = numero_doc;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Respuesta getResponse() {
		return response;
	}

	public void setResponse(Respuesta response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "AuditModel [api=" + api + ", fechaTransaccion=" + fechaTransaccion + ", logUUIDTxr=" + logUUIDTxr
				+ ", logUsuario=" + logUsuario + ", recursoApp=" + recursoApp + ", detalleR=" + detalleR
				+ ", idCliente=" + idCliente + ", nombreCliente=" + nombreCliente + ", codResponse=" + codResponse
				+ ", usuario=" + usuario + ", min_code=" + min_code + ", tipo_doc=" + tipo_doc + ", numero_doc="
				+ numero_doc + ", cuenta=" + cuenta + ", request=" + request + ", response=" + response + "]";
	}

}
