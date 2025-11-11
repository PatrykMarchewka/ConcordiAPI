package com.patrykmarchewka.concordiapi.Exceptions;

public class NoPrivilegesException extends RuntimeException{
    public NoPrivilegesException(){ super("You are not authorized to do that action"); }

    public NoPrivilegesException(String message){
        super(message);
    }
}