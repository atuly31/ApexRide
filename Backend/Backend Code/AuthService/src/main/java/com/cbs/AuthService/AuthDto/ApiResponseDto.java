package com.cbs.AuthService.AuthDto;


import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiResponseDto<T> {
    private String message;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    private HttpStatus status;
    private LocalDateTime timestamp;
    private T data;
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



    public ApiResponseDto(String message,HttpStatus status, LocalDateTime timestamp, T data) {
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
        this.status = status;
    }
}
