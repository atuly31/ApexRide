package com.cbs.Admin.Exception;

public class DuplicateDriverRegistrationException extends RuntimeException {
    public DuplicateDriverRegistrationException(String message) {
        super(message);
    }
   public DuplicateDriverRegistrationException(String message, Throwable cause) {
    super(message, cause);
  }
}
