package com.cbs.AuthService.Exception.Exception;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("Incorrect Password. Please retry."); // Default message
    }
    public IncorrectPasswordException(String message) {
        super(message); // Allows passing a specific message
    }


}
