package com.cbs.Driver.dto;

import java.time.LocalDateTime;

public class RideDto {

    private long id;
    private long userId;

    private long driverId;

    private String userFirstName;
    private String userlastName;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private  float rating;

    private String driverFullname;

    public String getDriverFullname() {
        return driverFullname;
    }
    public void setDriverFullname(String firstname , String lastnName) {
        String fullName = firstname + " " + lastnName;
        this.driverFullname = fullName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }
    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }


    public String getUserlastName() {
        return userlastName;
    }
    public void setUserlastName(String userlastName) {
        this.userlastName = userlastName;
    }


    private String pickupLocation;


    private String dropoffLocation;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private float actualFare;

    public  RideStatus status;

    public enum RideStatus {
        PENDING,          // User has requested a ride, but no driver search has started.
        SEARCHING_DRIVER, // The system is actively looking for an available driver.
        DRIVER_ASSIGNED,  // A driver has accepted the ride, but not yet started.
        RIDE_STARTED,          // The driver has picked up the passenger and the ride is in progress.
        COMPLETED,        // The ride has finished, passenger dropped off.
        CANCELLED_BY_USER, // User cancelled the ride.
        CANCELLED_BY_DRIVER, // Driver cancelled the ride.
        NO_DRIVER_FOUND,  // No driver could be found for the ride request.
        PAYMENT_PENDING,  // Ride completed, but payment is not yet processed.
        PAYMENT_COMPLETED // Ride completed and payment has been successfully processed.
    }
    public RideStatus getStatus() {
        return status;
    }
    public void setStatus(RideStatus status) {
        this.status = status;
    }


    public Long getDriverId() {
        return driverId;
    }
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }
    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public float getActualFare() {
        return actualFare;
    }
    public void setActualFare(float actualFare) {
        this.actualFare = actualFare;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
