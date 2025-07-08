package com.cbs.AuthService.Exception.Exception;

public class AlreadyExistException extends RuntimeException{

    public AlreadyExistException(String message){
        super(message);
    }
}
