package com.cbs.Ride.Dto;

import com.cbs.Ride.Entity.Rides;

import java.time.LocalDateTime;

public class RideDto {


    private long id;
    private long userId;
    private long driverId;
    private String userFirstName;
    private String userlastName;
    private String driverFullname;
    private  float rating;

    public float getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "RideDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", userFirstName='" + userFirstName + '\'' +
                ", userlastName='" + userlastName + '\'' +
                ", driverFullname='" + driverFullname + '\'' +
                ", rating=" + rating +
                ", distance=" + distance +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", dropoffLocation='" + dropoffLocation + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", actualFare=" + actualFare +
                ", status=" + status +
                '}';
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    private float distance;
    public String getDriverFullname() {
        return driverFullname;
    }

    public void setDriverFullname(String firstname, String lastnName) {
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

    public Rides.RideStatus getStatus() {
        return status;
    }

    public void setStatus(Rides.RideStatus status) {
        this.status = status;
    }

    public Rides.RideStatus status;

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
