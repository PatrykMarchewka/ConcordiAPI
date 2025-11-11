package com.patrykmarchewka.concordiapi.Exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Provided resource was not found on the server");
    }

    public NotFoundException(final String message){
        super(message);
    }
}
