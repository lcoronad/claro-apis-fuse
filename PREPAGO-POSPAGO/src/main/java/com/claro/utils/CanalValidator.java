package com.claro.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.claro.configuration.DataConfigCanales;
import com.claro.configuration.JacksonConfig;

@Component
public class CanalValidator implements ConstraintValidator<ValidatorEnum, String> {
	
	private List<Integer> valueList = null;
	
	
	private JacksonConfig jacksonConfig;
	
	private DataConfigCanales dataConfigCanales;
	
	@PostConstruct
	public void init() {
		System.out.println("ok inicio la clase");
	}
	
	@Autowired
	public CanalValidator() {
		System.out.println("build class ");
		this.jacksonConfig = new JacksonConfig();
		this.dataConfigCanales = new DataConfigCanales();
		
	}
	
	
	public CanalValidator(JacksonConfig jacksonConfig) {
		System.out.println("ok ingreso en build 2");
		this.jacksonConfig = jacksonConfig;
	}
	
	@Override
	public void initialize(ValidatorEnum validatorEnum) {
		valueList = new ArrayList<>();
		int[] data = validatorEnum.dataCanales();
//		List<Integer> test = this.dataConfigCanales.loadCanales();
//		System.out.println(test.size());
		System.out.println("Inyecto el bean +" + this.dataConfigCanales);
		System.out.println("Inyecto el bean +" + this.jacksonConfig);
		for (int i : data) {
			valueList.add(i);
		}
		
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		try {
			return valueList.contains(Integer.valueOf(value));
		} catch (Exception e) {
			return false;
		}
		
	}

}
