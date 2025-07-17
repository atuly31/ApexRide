package com.cbs.Driver.Exception;//package com.cbs.User.Exceptions;
 // Or a more appropriate package for your exception handlers


import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DriverDoesNotExistException.class)
    public ResponseEntity<com.cbs.Driver.Exception.ErrorResponseMessage> handleUserDoesNotExistException(
            DriverDoesNotExistException ex, WebRequest request) {


         ErrorResponseMessage errorResponseMessage= new ErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponseMessage, HttpStatus.NOT_FOUND);
    }
   @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<ErrorResponseMessage> handlePasswordIncorrectException(IncorrectPasswordException ex, WebRequest request) {
        ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseMessage, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(DriverNotAvailableException.class)
    public ResponseEntity<ErrorResponseMessage> handleDriverNotException(DriverNotAvailableException ex){
        ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseMessage,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignExceptions(FeignException ex) {
        // Log the full FeignException details here for debugging
        System.err.println("Driver Service Feign Exception Handler: " + ex.status() + " - " + ex.getMessage());
        System.err.println("Driver Service Feign Exception Content: " + ex.contentUTF8());

        // For production, consider sanitizing `ex.contentUTF8()`
        return ResponseEntity.status(ex.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.contentUTF8());
    }

    // Add a handler for your custom communication exception
    @ExceptionHandler(DriverServiceCommunicationException.class)
    public ResponseEntity<ErrorResponseMessage> handleDriverServiceCommunicationException(DriverServiceCommunicationException ex) {
        // You can extract details from the nested FeignException if needed
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        if (ex.getFeignCause() != null) {
            status = HttpStatus.valueOf(ex.getFeignCause().status());
            message = "Communication with Admin Service failed: " + ex.getFeignCause().contentUTF8();
            // Or parse the contentUTF8 if it's a known error structure.
        }

        ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage(
                message,
                status.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseMessage, status);
    }


}