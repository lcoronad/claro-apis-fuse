package com.claro.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DataConfigCanales {
	
	@Autowired
	private Environment env;
	
	@Bean
	public List<Integer> loadCanales() {
		System.out.println(env.getProperty("canales"));
		String[] canales = env.getProperty("canales").split(",");
		List<Integer> canalesList = new ArrayList<>();
		for (int i = 0; i < canales.length; i++) {
			canalesList.add(Integer.valueOf(canales[i]));
		}
		return canalesList;
	}

}
