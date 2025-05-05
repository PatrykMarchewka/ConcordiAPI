package com.patrykmarchewka.concordiapi.Exceptions;

public class JWTException extends RuntimeException {
    public JWTException(String message,Throwable cause) {
        super(message,cause);
    }
}
