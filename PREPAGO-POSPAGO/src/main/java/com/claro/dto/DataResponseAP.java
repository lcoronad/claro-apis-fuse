package com.claro.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize
public class DataResponseAP implements Serializable {

	private static final long serialVersionUID = 1L;

	public DataResponseItel response;

	public DataResponseItel getResponse() {
		return response;
	}

	public void setResponse(DataResponseItel response) {
		this.response = response;
	}

}
