package com.cbs.User.dto;

import java.time.LocalDateTime;

public class RideDto {

    private Long id;
    private Long userId;
    private String userFirstName; // Added to match constructor argument
    private String userlastName;  // Added to match constructor argument

    private Long driverId; // Added to match constructor argument
    private String driverFullname;

    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private float actualFare;
    private Float distance; // Added to match constructor argument
    public RideStatus status;
    private String vehicleInfo; // Added to accommodate 'car123' from test

    public enum RideStatus {
        PENDING,          // User has requested a ride, but no driver search has started.
        SEARCHING_DRIVER, // The system is actively looking for an available driver.
        DRIVER_ASSIGNED,  // A driver has accepted the ride, but not yet started.
        RIDE_STARTED,     // The driver has picked up the passenger and the ride is in progress.
        COMPLETED,        // The ride has finished, passenger dropped off.
        CANCELLED_BY_USER, // User cancelled the ride.
        CANCELLED_BY_DRIVER, // Driver cancelled the ride.
        NO_DRIVER_FOUND,  // No driver could be found for the ride request.
        PAYMENT_PENDING,  // Ride completed, but payment is not yet processed.
        PAYMENT_COMPLETED // Ride completed and payment has been successfully processed.
    }

    // Default (No-argument) constructor
    public RideDto() {
    }

    // Constructor to match the unit test's instantiation:
    // new RideDto(101L, 1L, "Source Location", "Destination Location", LocalDateTime.now(), LocalDateTime.now().plusHours(1), 50.0, "COMPLETED", "driver123", "car123")
    public RideDto(Long id, Long userId, String pickupLocation, String dropoffLocation,
                   LocalDateTime startTime, LocalDateTime endTime, float actualFare,
                   String status, String driverFullname, String vehicleInfo) { // vehicleInfo added here
        this.id = id;
        this.userId = userId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.actualFare = actualFare;
        this.status = RideStatus.valueOf(status); // Convert String to Enum
        this.driverFullname = driverFullname;
        this.vehicleInfo = vehicleInfo; // Assign vehicleInfo
        // userFirstName, userlastName, driverId, distance are not in the test's constructor call.
        // They will remain null or their default values if not explicitly set here or via setters.
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserlastName() {
        return userlastName;
    }

    public Long getDriverId() {
        return driverId;
    }

    public String getDriverFullname() {
        return driverFullname;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public float getActualFare() {
        return actualFare;
    }

    public Float getDistance() {
        return distance;
    }

    public RideStatus getStatus() {
        return status;
    }

    public String getVehicleInfo() { // Getter for vehicleInfo
        return vehicleInfo;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public void setUserlastName(String userlastName) {
        this.userlastName = userlastName;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    // Custom setter for driverFullName
    public void setDriverFullname(String firstname, String lastName) {
        this.driverFullname = firstname + " " + lastName;
    }

    // Standard setter for driverFullname (if you set it directly)
    public void setDriverFullname(String driverFullname) {
        this.driverFullname = driverFullname;
    }


    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setActualFare(float actualFare) {
        this.actualFare = actualFare;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public void setVehicleInfo(String vehicleInfo) { // Setter for vehicleInfo
        this.vehicleInfo = vehicleInfo;
    }
}