package com.cesar.User.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class CustomBadRequestException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public CustomBadRequestException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}