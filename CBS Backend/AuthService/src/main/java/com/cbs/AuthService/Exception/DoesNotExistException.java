package com.cbs.AuthService.Exception;

public class DoesNotExistException extends RuntimeException{

    public DoesNotExistException(String message){
              super(message);
    }
}
