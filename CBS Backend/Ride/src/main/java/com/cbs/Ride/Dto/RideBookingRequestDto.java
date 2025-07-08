package com.cbs.Ride.Dto;

public class RideBookingRequestDto {
    private String pickupLocation;

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

    public float getActualFare() {
        return actualFare;
    }

    public void setActualFare(float actualFare) {
        this.actualFare = actualFare;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    private String dropoffLocation;
    private float actualFare; // Match type from frontend (double for fare)

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    private Float distance;      // Match type from frontend (double for distance in km)
    private String duration;         // Match type from frontend (int for duration in minutes)

}
