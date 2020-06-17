/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.claro.esb.authentication.transformations;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.claro.esb.authentication.model.AuditModel;
import com.claro.esb.authentication.model.CustomException;
import com.claro.esb.authentication.model.Data;
import com.claro.esb.authentication.model.Request;
import com.claro.esb.authentication.model.Response;
import com.claro.esb.authentication.model.Respuesta;

@Component("transformations")
public class TransformationComponent {

	@Autowired
	private Environment env;

	private static final String DATE_ZONE = "America/Bogota";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public String proxy(Exchange ex) throws ClientProtocolException, IOException {
		String request = ex.getIn().getBody(String.class);
		HttpHost proxy = new HttpHost(env.getProperty("sso.proxy.host"),
				Integer.parseInt(env.getProperty("sso.proxy.port")));
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

		// Client credentials
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new NTCredentials(env.getProperty("sso.proxy.user"), env.getProperty("sso.proxy.password"),
						env.getProperty("sso.proxy.host") + ":" + env.getProperty("sso.proxy.port"),
						env.getProperty("sso.proxy.domain")));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();

		BasicScheme basicAuth = new BasicScheme();
		authCache.put(proxy, basicAuth);
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credentialsProvider);
		context.setAuthCache(authCache);

		HttpClient httpclient = HttpClients.custom().setRoutePlanner(routePlanner)
				.setDefaultCredentialsProvider(credentialsProvider).build();

		HttpPost httpGet = new HttpPost(env.getProperty("sso.url"));
		StringEntity entity = new StringEntity(request);
		httpGet.setEntity(entity);
		httpGet.setHeader("Content-Type", env.getProperty("sso.contentType"));
		httpGet.setHeader("SOAPAction", env.getProperty("sso.soapAction"));
		HttpResponse response = httpclient.execute(httpGet, context);
		System.out.println("Status:" + response.getStatusLine());
		return EntityUtils.toString(response.getEntity());
	}

	public void setAuthenticationType(Exchange ex) throws Exception {
		Data req = ex.getIn().getBody(Request.class).getData();
		ex.getIn().setHeader("RequestedAuthenticationTypes", getRequestedAuthenticationTypes(ex));
		if (getRequestedAuthenticationTypes(ex).size() == 1) {
			// auth by user and password
			if (req.getUsuario() != null) {
				if (req.getContrasena() != null) {
					ex.getIn().setHeader("authenticationType", "user_password");
				} else {
					throw new Exception("Debe digitar una contraseña");
				}
			} else
			// auth by min
			if (req.getMin() != null) {
				ex.getIn().setHeader("authenticationType", "min");
			} else

			// auth by doctype and num doc
			if (req.getTipoIdentificacion() != null) {
				if (req.getNumeroIdentificacion() != null) {
					ex.getIn().setHeader("authenticationType", "typeDoc_numDoc");
				} else {
					throw new Exception("Debe Digitar un número de identificación");
				}
			} else

			// auth by account
			if (req.getCuenta() != null) {
				ex.getIn().setHeader("authenticationType", "accountNumber");
			} else {
				throw new CustomException("No se selecciono ningun tipo de autenticacion");
			}
		} else {
			ex.getIn().setHeader("authenticationType", "multiple");
		}
	}

	private List<String> getRequestedAuthenticationTypes(Exchange ex) {
		Data req = ex.getIn().getBody(Request.class).getData();
		List<String> types = new ArrayList<String>();
		if (req.getUsuario() != null) {
			ex.getIn().setHeader("isUserAuth", true);
			types.add("user_password");
		}

		if (req.getMin() != null) {
			ex.getIn().setHeader("isMinAuth", true);
			types.add("min");
		}

		if (req.getTipoIdentificacion() != null) {
			ex.getIn().setHeader("isIdAuth", true);
			types.add("typeDoc_numDoc");
		}

		if (req.getCuenta() != null) {
			ex.getIn().setHeader("isAccountAuth", true);
			types.add("accountNumber");
		}
		return types;
	}

	private void validateBegin(String txt, String msg) {
		if (!txt.startsWith("3")) {
			throw new CustomException(msg);
		}
	}

	private void validateNotEmpty(String txt, String msg) {
		if (txt.trim().equals("")) {
			throw new CustomException(msg);
		}
	}

	private boolean consecutiveNumbers(String cadena) {
		for (int i = 0; i < cadena.length() - 1; i++) {
			int valor1 = (int) cadena.charAt(i);
			int valor2 = (int) cadena.charAt(i + 1);
			if (valor1 + 1 != valor2)
				return false;
		}
		return true;
	}

	public void validateFields(Exchange ex) {
		Data req = ex.getIn().getBody(Request.class).getData();

		switch (ex.getIn().getHeader("authenticationType").toString()) {
		case "accountNumber":
			validateNotEmpty(req.getCuenta().toString(), "Debe digitar el número de cuenta");
			validateLength(10, 10, req.getCuenta().toString(), "Número de cuenta no válido");
			break;
		case "min":
			validateNotEmpty(req.getMin().toString(), "Debe digitar el número de celular");
			validateBegin(req.getMin().toString(), "El número celular debe iniciar por el número tres (3).");
			if (!req.getMin().toString().matches("^[0-9]*$")) {
				throw new CustomException("El número celular no puede contener letras ni caracteres especiales");
			}
			break;
		case "typeDoc_numDoc":
			// validacion de campos no vacios
			validateNotEmpty(req.getTipoIdentificacion().toString(), "Debe seleccionar un tipo de identificación");
			validateNotEmpty(req.getNumeroIdentificacion().toString(), "Debe digitar un número de identificación");
			// Validacion por tipo de documento enviado
			switch (req.getTipoIdentificacion()) {
			case "1": // Cedula de ciudadania
				validateNotEmpty(req.getNumeroIdentificacion().toString(), "Debe Digitar un número de identificación");
				if (!req.getNumeroIdentificacion().toString().matches("^[A-Za-z0-9 ]*$")) {
					throw new CustomException("El número de identificación no puede contener caracteres especiales");
				}
				if (!req.getNumeroIdentificacion().toString().matches("^(?!\\\\s*$)[-a-zA-Z0-9_:,.'']{1,100}$")) {
					throw new CustomException("El número de identificación no puede contener espacios");
				}
				if (!req.getNumeroIdentificacion().toString().matches("^[0-9]*$")) {
					throw new CustomException("Debe digitar números");
				}
				validateLength(5, 10, req.getNumeroIdentificacion().toString(), "Los campos deben ser tipo numérico");
				if (consecutiveNumbers(req.getNumeroIdentificacion().toString())) {
					throw new CustomException("los dígitos no pueden ser consecutivos");
				}
				if (!req.getNumeroIdentificacion().toString().matches("^[1-9]\\d*$")) {
					throw new CustomException("El número de identificación no puede comenzar por cero (0)");
				}

				break;
			case "2": // Cedula de extranjeria
				validateNotEmpty(req.getNumeroIdentificacion().toString(), "Debe Digitar un número de identificación");
				validateStrictLength(5, 10, req.getNumeroIdentificacion().toString(),
						"Los campos deben ser tipo numérico");
				if (!req.getNumeroIdentificacion().toString().matches("^[A-Za-z0-9 ]*$")) {
					throw new CustomException("El número de identificación no puede contener caracteres especiales");
				}
				if (!req.getNumeroIdentificacion().toString().matches("^(?!\\\\s*$)[-a-zA-Z0-9_:,.'']{1,100}$")) {
					throw new CustomException("El número de identificación no puede contener espacios");
				}
				if (!req.getNumeroIdentificacion().toString().matches("^[0-9]*$")) {
					throw new CustomException("Debe digitar números");
				}
				if (consecutiveNumbers(req.getNumeroIdentificacion().toString())) {
					throw new CustomException("los dígitos no pueden ser consecutivos");
				}
				if (!req.getNumeroIdentificacion().toString().matches("^[1-9]\\d*$")) {
					throw new CustomException("El número de identificación no puede comenzar por cero (0)");
				}
				break;
			case "3": // Nit
				validateNotEmpty(req.getNumeroIdentificacion().toString(), "Debe Digitar un número de identificación");
				if (!req.getNumeroIdentificacion().toString().matches("^[A-Za-z0-9-]*$")) {
					throw new CustomException("El número de identificación no puede contener caracteres especiales");
				}
				if (!req.getNumeroIdentificacion().toString().matches("^(?!\\\\s*$)[-a-zA-Z0-9_:,.'']{1,100}$")) {
					throw new CustomException("El número de identificación no puede contener espacios");
				}
				if (!req.getNumeroIdentificacion().toString().matches("^[0-9-]*$")) {
					throw new CustomException("Debe digitar números");
				}
				if (req.getNumeroIdentificacion().toString().startsWith("0")) {
					throw new CustomException("El número de identificación no puede comenzar por cero (0)");
				}
				// get the data (without '-')
				String data = "";
				if (req.getNumeroIdentificacion().contains("-")) {
					data = req.getNumeroIdentificacion().split("-")[0];
				} else {
					data = req.getNumeroIdentificacion();
				}
				long n = Long.parseLong(data);
				if (n >= 800000000 && n <= 999999999) {
					// ok
					return;
				} else if (n > 8000000000L && n < 9999999999L) {
					// ok
					return;
				} else {
					throw new CustomException("Digite un número de NIT válido");
				}
			case "4": // Pasaporte
				validateLength(5, 10, req.getNumeroIdentificacion().toString(), null);
				break;

			default:
				throw new CustomException("Se debe seleccionar solo una opción de 1 a 4");
			}
			break;
		case "user_password":
			validateNotEmpty(req.getUsuario().toString(), "Debe digitar un usuario");
			validateNotEmpty(req.getContrasena().toString(), "Debe digitar una contraseña");
			if (!req.getContrasena().toString().matches("^(?!\\\\s*$)[-a-zA-Z0-9_:,.'']{1,100}$")) {
				throw new CustomException("La contraseña no debe contener espacios en blanco");
			}
			break;
		default:
			break;
		}

	}

	private void validateStrictLength(int min, int max, String data, String msg) {
		if (data.length() > max || data.length() < min) {
			throw new CustomException(msg);
		}
	}

	private void validateLength(int min, int max, String data, String msg) {
		if (data.length() > 10) {
			throw new CustomException("El número de identificación no puede ser mayor a " + max + " caracteres");
		}
		if (data.length() < 5) {
			throw new CustomException("El número de identificación no puede ser menor a " + min + " caracteres");
		}
		if (msg != null && !data.matches("[0-9]+")) {
			throw new CustomException(msg);
		}
	}

	public void loadRRInfo(Exchange ex) {
		// Headers to template
		if (ex.getIn().getHeader("authenticationType").equals("typeDoc_numDoc")) {
			String type = "";
			switch (ex.getIn().getBody(Request.class).getData().getTipoIdentificacion()) {
			case "1":
				type = "CC";
				break;
			case "2":
				type = "CE";
				break;
			case "3":
				type = "NIT";
				break;
			case "4":
				type = "PP";
				break;
			default:
				break;
			}
			ex.getIn().setHeader("valorConsulta",
					type + ex.getIn().getBody(Request.class).getData().getNumeroIdentificacion());
		} else if (ex.getIn().getHeader("authenticationType").equals("accountNumber")) {
			ex.getIn().setHeader("valorConsulta", ex.getIn().getBody(Request.class).getData().getCuenta());
		} else {
			// auth is multiple
			if (ex.getIn().getBody(Request.class).getData().getCuenta() != null) {
				ex.getIn().setHeader("tipoConsulta", "SU");
				ex.getIn().setHeader("valorConsulta", ex.getIn().getBody(Request.class).getData().getCuenta());
			} else if (ex.getIn().getBody(Request.class).getData().getTipoIdentificacion() != null) {
				String type = "";
				switch (ex.getIn().getBody(Request.class).getData().getTipoIdentificacion()) {
				case "1":
					type = "CC";
					break;
				case "2":
					type = "CE";
					break;
				case "3":
					type = "NIT";
					break;
				case "4":
					type = "NIT";
					break;
				default:
					break;
				}
				ex.getIn().setHeader("tipoConsulta", "DO");
				ex.getIn().setHeader("valorConsulta",
						type + ex.getIn().getBody(Request.class).getData().getNumeroIdentificacion());
			}
		}
	}

	public void loadESBInfo(Exchange ex) {
		// Headers to template
		ex.getIn().setHeader("password", ex.getIn().getBody(Request.class).getData().getContrasena());
		ex.getIn().setHeader("username", ex.getIn().getBody(Request.class).getData().getUsuario());
	}

	public void buildResponse(Exchange ex) {
		Response resp = new Response();
		Respuesta respuesta = new Respuesta();
		respuesta.setCodigoRespuesta(ex.getIn().getHeader("codigoRespuesta").toString());
		respuesta.setMensajeRespuesta(ex.getIn().getHeader("mensajeRespuesta").toString());
		resp.setRespuesta(respuesta);
		ex.getIn().setBody(resp);
	}

	@SuppressWarnings("unchecked")
	public void getBSCSMinResponse(Exchange ex) {
		Map<String, String> sqlData = ex.getIn().getBody(Map.class);
		ex.getIn().setHeader("minResponse", sqlData.get("S_vcs_stat_chng"));
	}

	@SuppressWarnings("unchecked")
	public void buildMultipleAuthResponse(Exchange ex) {

		boolean ban = false;
		ArrayList<Map<String, Object>> listResults = ex.getIn().getBody(ArrayList.class);
		for (Map<String, Object> map : listResults) {
			if (!ban) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if ((boolean) entry.getValue()) {
						ban = true;
						break;
					}
				}
			} else {
				break;
			}
		}

		if (ban) {
			ex.getIn().setHeader("multipleOk", true);
			ex.getIn().setHeader("codigoRespuesta", "OK");
			ex.getIn().setHeader("mensajeRespuesta", "Usuario Válido");
		} else {
			ex.getIn().setHeader("multipleOk", false);
			ex.getIn().setHeader("codigoRespuesta", "OK");
			ex.getIn().setHeader("mensajeRespuesta", "Error en los datos proporcionados");
		}
	}

	public void auditAccount(Exchange ex) {
		AuditModel audit = loadGlobalInfo(ex);
		Request req = (Request) ex.getIn().getHeader("sentRequest");
		Data data = req.getData();
		audit.setCuenta((String) data.getCuenta());
		ex.getIn().setHeader("auditData", audit);
	}

	public void auditMin(Exchange ex) {
		AuditModel audit = loadGlobalInfo(ex);
		Request req = (Request) ex.getIn().getHeader("sentRequest");
		Data data = req.getData();
		audit.setMin_code(data.getMin());
		ex.getIn().setHeader("auditData", audit);
	}

	public void auditUserPass(Exchange ex) {
		AuditModel audit = loadGlobalInfo(ex);
		Request req = (Request) ex.getIn().getHeader("sentRequest");
		Data data = req.getData();
		audit.setUsuario((String) data.getUsuario());
		ex.getIn().setHeader("auditData", audit);
	}

	public void auditId(Exchange ex) {
		AuditModel audit = loadGlobalInfo(ex);
		Request req = (Request) ex.getIn().getHeader("sentRequest");
		Data data = req.getData();
		audit.setTipo_doc(data.getTipoIdentificacion());
		audit.setNumero_doc(data.getNumeroIdentificacion());
		ex.getIn().setHeader("auditData", audit);
	}

	public static String fechaT() {
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(DATE_ZONE));
		return DateTimeFormatter.ofPattern(DATE_FORMAT).format(zonedDateTime);
	}

	private AuditModel loadGlobalInfo(Exchange ex) {
		AuditModel audit = new AuditModel();
		audit.setApi(ex.getIn().getHeader("api_name").toString());
		audit.setFechaTransaccion(fechaT());
		audit.setLogUUIDTxr(ex.getExchangeId());
		audit.setLogUsuario("SYSTEM");
		audit.setRecursoApp(ex.getIn().getHeader("CamelServletContextPath").toString());
		audit.setIdCliente((String) ex.getIn().getHeader("ClientID")); // Header de 3Scale
		audit.setNombreCliente((String) ex.getIn().getHeader("NombreCliente")); // Header de Scale
		audit.setRequest((Request) ex.getIn().getHeader("sentRequest"));
		audit.setCodResponse((String) ex.getIn().getHeader("codigoRespuesta"));
		audit.setDetalleR((String) ex.getIn().getHeader("mensajeRespuesta"));
		audit.setResponse(ex.getIn().getBody(Response.class).getRespuesta());
		return audit;
	}

	@SuppressWarnings("unchecked")
	public void isActiveFromOnyx(Exchange ex) {
		List<Map<String, String>> sqlData = ex.getIn().getBody(List.class);
		ex.getIn().setHeader("userIsActive", sqlData.get(0).get("Estado cliente "));
	}
}
