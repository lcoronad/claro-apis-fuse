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
package com.claro.esb.authentication.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sso")
public class WSSSO {

	private String url;
	private String method;
	private String contentType;
	private String timeout;
	private String soapAction;
	private String contrasenaAutenticacion;
	private String tipoCanalID;
	private String usuarioAutenticacion;

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContrasenaAutenticacion() {
		return contrasenaAutenticacion;
	}

	public void setContrasenaAutenticacion(String contrasenaAutenticacion) {
		this.contrasenaAutenticacion = contrasenaAutenticacion;
	}

	public String getTipoCanalID() {
		return tipoCanalID;
	}

	public void setTipoCanalID(String tipoCanalID) {
		this.tipoCanalID = tipoCanalID;
	}

	public String getUsuarioAutenticacion() {
		return usuarioAutenticacion;
	}

	public void setUsuarioAutenticacion(String usuarioAutenticacion) {
		this.usuarioAutenticacion = usuarioAutenticacion;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	@Override
	public String toString() {
		return "RestESB [url=" + url + ", method=" + method + ", contentType=" + contentType + ", timeout=" + timeout
				+ ", contrasenaAutenticacion=" + contrasenaAutenticacion + ", tipoCanalID=" + tipoCanalID
				+ ", usuarioAutenticacion=" + usuarioAutenticacion + "]";
	}

}
