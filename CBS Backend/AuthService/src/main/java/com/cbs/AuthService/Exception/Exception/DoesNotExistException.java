package com.cbs.AuthService.Exception.Exception;

public class DoesNotExistException extends RuntimeException{

    public DoesNotExistException(String message){
              super(message);
    }
}
