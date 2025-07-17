package com.cbs.AuthService.Exception;

public class AlreadyExistException extends RuntimeException{

    public AlreadyExistException(String message){
        super(message);
    }
}
