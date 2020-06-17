package com.claro.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CanalValidator.class)
public @interface ValidatorEnum {

	int[] dataCanales();

	String message() default "Error";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
