package com.cesar.ChatWeb.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomEmailValidator.class)
public @interface CustomEmail {

	public String message() default "Email is not valid";

	Class<?>[] groups() default {};

	Class <? extends Payload>[] payload() default {};

}
