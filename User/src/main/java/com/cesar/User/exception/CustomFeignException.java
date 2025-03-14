package com.cesar.User.exception;

public class CustomFeignException extends RuntimeException {
    public CustomFeignException(String message){
        super(message);
    }
}