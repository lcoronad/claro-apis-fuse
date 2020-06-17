package com.claro.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
@JsonIgnoreProperties({"dirección"})
public class DataResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4346895329249634507L;
	
	@JsonProperty("error")
	public transient JsonNode error = JsonNodeFactory.instance.objectNode();
	
	@JsonProperty("factura")
	public transient JsonNode factura = JsonNodeFactory.instance.objectNode();
	
	
	
	

}
