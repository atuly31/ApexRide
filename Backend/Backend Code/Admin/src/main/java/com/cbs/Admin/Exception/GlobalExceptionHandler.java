package com.cbs.Admin.Exception;

import com.cbs.Admin.AdminDTO.ErrorResponseMessage;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ErrorResponseMessage> handleDriverNotFoundException(DriverNotFoundException ex, WebRequest request) {
        ErrorResponseMessage error = new ErrorResponseMessage(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicateDriverRegistrationException, returning a 409 Conflict response.
     * @param ex The DuplicateDriverRegistrationException instance.
     * @param request The current web request.
     * @return A ResponseEntity containing an ErrorResponseMessage and HTTP status 409.
     */
    @ExceptionHandler(DuplicateDriverRegistrationException.class)
    public ResponseEntity<ErrorResponseMessage> handleDuplicateDriverRegistrationException(DuplicateDriverRegistrationException ex, WebRequest request) {
        ErrorResponseMessage error = new ErrorResponseMessage(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponseMessage> handleGlobalException(Exception ex, WebRequest request) {
//        ErrorResponseMessage error = new ErrorResponseMessage(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                "An unexpected error occurred: " + ex.getMessage(),
//                request.getDescription(false).replace("uri=", "")
//        );
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignExceptions(FeignException ex) {

        System.err.println("Admin Service Feign Exception Handler: " + ex.status() + " - " + ex.getMessage());
        System.err.println("Admin Service Feign Exception Content: " + ex.contentUTF8());


        return ResponseEntity.status(ex.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.contentUTF8()); // This returns the raw JSON from the called service
    }
}
