package com.cassol.rinhadebackend.api;

import com.cassol.rinhadebackend.exceptions.BusinessRuleException;
import com.cassol.rinhadebackend.exceptions.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(EntityNotFoundException ex) {
        log.debug("Resource not found = {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<String> handleBusinessRuleException(BusinessRuleException ex) {
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.debug("Validation errors = {}", errors);
        return errors;
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleNoHandlerFoundException(
        Exception ex, HttpServletRequest httpServletRequest) {
        log.debug("No handler found for request = {} exception={} msg={}", httpServletRequest.getRequestURI(), ex.getClass(),  ex.getMessage());
        if(ex instanceof NoResourceFoundException){
            return ResponseEntity.unprocessableEntity().body(ex.getMessage());
        }
        if(ex instanceof ObjectOptimisticLockingFailureException){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }

}
