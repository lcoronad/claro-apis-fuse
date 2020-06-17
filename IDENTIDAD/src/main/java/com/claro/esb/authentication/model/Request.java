package com.claro.esb.authentication.model;

import javax.validation.Valid;

public class Request {
	
	@Valid
	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ClassPojo [data = " + data + "]";
	}
}