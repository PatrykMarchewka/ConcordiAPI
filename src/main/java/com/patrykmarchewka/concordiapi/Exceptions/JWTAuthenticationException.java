package com.patrykmarchewka.concordiapi.Exceptions;

import org.springframework.security.core.AuthenticationException;

public class JWTAuthenticationException extends AuthenticationException {
    public JWTAuthenticationException(String message) {
        super(message);
    }


    /**
     * Suppresses stack trace to avoid log clutter
     * Stack trace is intentionally omitted as this exception is thrown as a signal not a critical system error
     * @return Empty stack trace
     */
    @Override
    public synchronized Throwable fillInStackTrace(){
        return this;
    }

}
