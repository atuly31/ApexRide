package com.cbs.Ride.Exception;//package com.cbs.User.Exceptions;
// Or a more appropriate package for your exception handlers


import com.cbs.Ride.Dto.ApiResponseDto;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ErrorResponseMessage> handleRideNotFoundException(RideNotFoundException ex){
        ErrorResponseMessage message = new ErrorResponseMessage(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignExceptions(FeignException ex) {
        return ResponseEntity.status(ex.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.contentUTF8()); // <--- This line returns the exact raw JSON from the User Service
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                new ApiResponseDto<>(
                        "An unexpected internal server error occurred. Please try again later or contact support.",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        LocalDateTime.now(),
                        null
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


}