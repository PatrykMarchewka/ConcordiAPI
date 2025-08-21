package com.patrykmarchewka.concordiapi.Exceptions;

public class ImpossibleStateException extends RuntimeException {
    public ImpossibleStateException(String message) {
        super(message);
    }
}
