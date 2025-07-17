package com.cbs.Payment.dto;

public enum DriverStatus{
    AVAILABLE,    // Driver is online and ready to accept rides.
    ON_RIDE,      // Driver is currently on an active ride.
    OFFLINE,      // Driver is logged out or not available.
    BUSY,         // Driver is online but temporarily busy (e.g., break, fueling).
    SUSPENDED
}