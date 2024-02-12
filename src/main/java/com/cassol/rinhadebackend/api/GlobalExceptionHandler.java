package com.cassol.rinhadebackend.api;

import com.cassol.rinhadebackend.exceptions.BusinessRuleException;
import com.cassol.rinhadebackend.exceptions.EntityNotFoundException;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(EntityNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(404);
        problemDetail.setDetail(ex.getMessage());
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<String> handleBusinessRuleException(BusinessRuleException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(422);
        problemDetail.setDetail(ex.getMessage());
        return ResponseEntity.of(problemDetail).build();
    }


}
