package com.claro.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(namespace = "ns2=\"http://tempuri.org/\"", localName = "ns2:BankPaymentReferenceInformation")
public class ResponseWsdlFactura implements Serializable{
	
	
	private static final long serialVersionUID = -5223637585783289735L;
	
	@JacksonXmlProperty( namespace = "ns1=\"http://schemas.datacontract.org/2004/07/BankPaymentManagement\"",localName = "ns1:bankCode")
	public String body;
	
	@JsonIgnore
	@JacksonXmlProperty(namespace="http://schemas.xmlsoap.org/soap/envelope/", localName = "body")
	public Map<String, Object> data = new HashMap<>();
	
	
	
	

}
