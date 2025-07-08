// src/main/java/com/cbs/User/Exceptions/IncorrectPasswordException.java
package com.cbs.User.Exceptions;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("Incorrect Password or Username. Please retry."); // Default message
    }

    public IncorrectPasswordException(String message) {
        super(message); // Allows passing a specific message
    }

    public IncorrectPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}