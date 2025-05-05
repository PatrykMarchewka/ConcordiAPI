package com.patrykmarchewka.concordiapi.Exceptions;

public class WrongCredentialsException extends RuntimeException {
    public WrongCredentialsException() {
        super("Invalid credentials");
    }
}
