package com.cbs.User.dto;

import java.time.LocalDateTime; // Import LocalDateTime

public class RideBookingRequestDto {
    private String pickupLocation;
    private String dropoffLocation;
    private float actualFare;
    private String distance;
    private String duration;
    private LocalDateTime desiredPickupTime; // Added to match test constructor

    // Default (No-argument) constructor
    public RideBookingRequestDto() {
    }

    // Constructor to match the unit test's instantiation:
    // new RideBookingRequestDto("Source Location", "Destination Location", LocalDateTime.now().plusHours(1))
    public RideBookingRequestDto(String pickupLocation, String dropoffLocation, LocalDateTime desiredPickupTime) {
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.desiredPickupTime = desiredPickupTime;
        // You might want to initialize other fields to default values if not provided
        this.actualFare = 0.0f; // Example default
        this.distance = null;
        this.duration = null;
    }

    // --- Getters ---
    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public float getActualFare() {
        return actualFare;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public LocalDateTime getDesiredPickupTime() {
        return desiredPickupTime;
    }

    // --- Setters ---
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public void setActualFare(float actualFare) {
        this.actualFare = actualFare;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDesiredPickupTime(LocalDateTime desiredPickupTime) {
        this.desiredPickupTime = desiredPickupTime;
    }
}