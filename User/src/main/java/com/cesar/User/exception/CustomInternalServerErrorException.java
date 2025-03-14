package com.cesar.User.exception;

public class CustomInternalServerErrorException extends RuntimeException {
    public CustomInternalServerErrorException(String message){
        super(message);
    }
}