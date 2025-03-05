package com.cesar.Media.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> internalServer(RuntimeException ex){
		return ResponseEntity.internalServerError().body(ex.getMessage());
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<?> badRequest(BadRequestException ex){
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
