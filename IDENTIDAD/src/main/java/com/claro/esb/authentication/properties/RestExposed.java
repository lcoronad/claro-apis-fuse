package com.claro.esb.authentication.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest")
public class RestExposed {

	private String apiPath;
	private String apiTitle;
	private String apiVersion;
	private String apiBasePath;
	private String serviceName;
	private String serviceNameId;
	private String healthcheckServiceName;
	private String inputDescription;
	private String serviceDescription;

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public String getApiTitle() {
		return apiTitle;
	}

	public void setApiTitle(String apiTitle) {
		this.apiTitle = apiTitle;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getApiBasePath() {
		return apiBasePath;
	}

	public void setApiBasePath(String apiBasePath) {
		this.apiBasePath = apiBasePath;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceNameId() {
		return serviceNameId;
	}

	public void setServiceNameId(String serviceNameId) {
		this.serviceNameId = serviceNameId;
	}

	public String getHealthcheckServiceName() {
		return healthcheckServiceName;
	}

	public void setHealthcheckServiceName(String healthcheckServiceName) {
		this.healthcheckServiceName = healthcheckServiceName;
	}

	public String getInputDescription() {
		return inputDescription;
	}

	public void setInputDescription(String inputDescription) {
		this.inputDescription = inputDescription;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

}
