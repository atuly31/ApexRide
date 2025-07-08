package com.cbs.Ride.Exception;

public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException(String message){
        super(message);
    }
}
