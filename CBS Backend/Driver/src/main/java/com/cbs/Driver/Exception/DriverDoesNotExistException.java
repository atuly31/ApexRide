package com.cbs.Driver.Exception;

public class DriverDoesNotExistException extends RuntimeException{

    public DriverDoesNotExistException(String message){
              super(message);
    }
}
