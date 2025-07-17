package com.cbs.Payment.dto;

public enum RideStatus {
    PENDING,          // User has requested a ride, but no driver search has started.
    SEARCHING_DRIVER, // The system is actively looking for an available driver.
    DRIVER_ASSIGNED,  // A driver has accepted the ride, but not yet started.
    RIDE_STARTED,          // The driver has picked up the passenger and the ride is in progress.
    COMPLETED,        // The ride has finished, passenger dropped off.
    CANCELLED_BY_USER, // User cancelled the ride.
    CANCELLED_BY_DRIVER, // Driver cancelled the ride.
    NO_DRIVER_FOUND,  // No driver could be found for the ride request.
}
