package com.claro.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

import com.claro.dto.Data;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class ConsistentValuesParameterValidator implements ConstraintValidator<ValidParameters, Object> {

	@Override
	public void initialize(ValidParameters constraintAnnotation) {
		//add implements
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		Data data = (Data) value;
		
		if(data.idReferencia.trim().isEmpty() && data.min.trim().isEmpty()) {
			return false;
		}else {
			return true;
		}

	}

}
