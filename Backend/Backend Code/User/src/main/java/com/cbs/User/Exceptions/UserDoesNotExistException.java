package com.cbs.User.Exceptions;

public class UserDoesNotExistException  extends RuntimeException{
    public UserDoesNotExistException(String message){
        super(message);
    }

}
