package com.cesar.ChatWeb.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomEmailValidator implements ConstraintValidator<CustomEmail, String> {


	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {

		return (email.matches("^[A-Za-z0-9]+@[A-Za-z]+\\.[A-Za-z]{2,3}$"));

	}


}
