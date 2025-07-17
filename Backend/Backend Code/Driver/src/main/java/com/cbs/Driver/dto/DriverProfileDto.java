package com.cbs.Driver.dto;

public class DriverProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;
    private String password;
    private String phoneNumber;
    private String licenseNumber;
    private String vehicleModel;
    private String licensePlate;
    private boolean isApproved;
    private float rating;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    private String passwordHash;
    public DriverRegistrationDto.DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverRegistrationDto.DriverStatus status) {
        this.status = status;
    }

    public DriverRegistrationDto.DriverStatus status;

    public enum DriverStatus{
        AVAILABLE,    // Driver is online and ready to accept rides.
        ON_RIDE,      // Driver is currently on an active ride.
        OFFLINE,      // Driver is logged out or not available.
        BUSY,         // Driver is online but temporarily busy (e.g., break, fueling).
        SUSPENDED
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }


}
