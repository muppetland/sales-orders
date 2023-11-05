package com.liverpool.orders.exceptions;


import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.hibernate.query.IllegalQueryOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage()
                .builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(new Date())
                .description(request.getDescription(false))
                .build();
        return new ResponseEntity<ErrorMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage()
                .builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(new Date())
                .description(request.getDescription(false))
                .build();
        return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(ResourceAccessException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage()
                .builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.LOCKED.value())
                .timestamp(new Date())
                .description(request.getDescription(false))
                .build();
        return new ResponseEntity<ErrorMessage>(message, HttpStatus.LOCKED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(BadRequestException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage()
                .builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(new Date())
                .description(request.getDescription(false))
                .build();
        return new ResponseEntity<ErrorMessage>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalQueryOperationException.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(IllegalQueryOperationException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage()
                .builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .timestamp(new Date())
                .description(request.getDescription(false))
                .build();
        return new ResponseEntity<ErrorMessage>(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(RuntimeException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage()
                .builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.SEE_OTHER.value())
                .timestamp(new Date())
                .description(request.getDescription(false))
                .build();
        return new ResponseEntity<ErrorMessage>(message, HttpStatus.SEE_OTHER);
    }
}
