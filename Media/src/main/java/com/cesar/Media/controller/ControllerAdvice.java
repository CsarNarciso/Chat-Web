package com.cesar.Media.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<?> mediaTypeNotSupported(HttpMediaTypeNotSupportedException ex){
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ex.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> internalServer(RuntimeException ex){
		return ResponseEntity.internalServerError().body(ex.getMessage());
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<?> badRequest(BadRequestException ex){
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
