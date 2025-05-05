package com.patrykmarchewka.concordiapi.Exceptions;

import com.patrykmarchewka.concordiapi.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    //Wrong data
    //Conflict data



    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<String>> handleGeneric(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>("Unexpected error!",ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse<String>> handleRuntime(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>("Unexpected error!", ex.getMessage()));
    }

    @ExceptionHandler(WrongCredentialsException.class)
    public ResponseEntity<APIResponse<Void>> handleWrongCred(WrongCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(NoPrivilegesException.class)
    public ResponseEntity<APIResponse<Void>> handleNoPriv(NoPrivilegesException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<APIResponse<String>> handleConflict(ConflictException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIResponse<>("Conclift occured",ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<APIResponse<Void>> handleNotFound(NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(ex.getMessage(),null));
    }


}
