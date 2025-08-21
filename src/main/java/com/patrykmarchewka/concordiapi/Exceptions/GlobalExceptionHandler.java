package com.patrykmarchewka.concordiapi.Exceptions;

import com.patrykmarchewka.concordiapi.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<String>> handleGeneric(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>("Unexpected error!",ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse<String>> handleRuntime(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>("Unexpected error!", ex.getMessage()));
    }

    @ExceptionHandler(WrongCredentialsException.class)
    public ResponseEntity<APIResponse<String>> handleWrongCred(WrongCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(NoPrivilegesException.class)
    public ResponseEntity<APIResponse<String>> handleNoPriv(NoPrivilegesException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new APIResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(JWTException.class)
    public ResponseEntity<APIResponse<String>> handleJWT(JWTException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(ex.getMessage(),ex.getCause().toString()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<APIResponse<String>> handleConflict(ConflictException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIResponse<>("Conclift occured",ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<APIResponse<String>> handleNotFound(NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(ex.getMessage(),null));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIResponse<String>> handleBadRequest(BadRequestException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>("Validation failed", errors));
    }

    @ExceptionHandler(ImpossibleStateException.class)
    public ResponseEntity<APIResponse<String>> handleImpossible(ImpossibleStateException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIResponse<>(ex.getMessage(), null));
    }
}
