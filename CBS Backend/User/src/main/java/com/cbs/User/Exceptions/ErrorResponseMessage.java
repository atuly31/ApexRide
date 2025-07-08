package com.cbs.User.Exceptions;

import java.time.LocalDateTime;

public class ErrorResponseMessage {

    private String message;       // Stores the error message
    private int status;           // Stores the HTTP status code
    private LocalDateTime timestamp;       // Stores the time when the error occurred

    // Constructor to initialize the fields
    public ErrorResponseMessage(String message, int status, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters to access the fields
    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
