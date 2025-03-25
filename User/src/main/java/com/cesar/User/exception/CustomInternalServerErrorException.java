package com.cesar.User.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class CustomInternalServerErrorException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public CustomInternalServerErrorException(String message, HttpStatusCode statusCode){
        super(message);
        this.statusCode = statusCode;
    }
}