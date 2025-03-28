package com.cesar.User.controller;

import com.cesar.User.exception.CustomBadRequestException;
import com.cesar.User.exception.CustomInternalServerErrorException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<?> badRequest(CustomBadRequestException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CustomInternalServerErrorException.class)
    public ResponseEntity<?> internalServer(CustomInternalServerErrorException ex){
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}