package com.cbs.Ride.Dto;


public class AvailableDriverDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private String phoneNumber;
    private String licenseNumber;
    private String vehicleModel;
    private String licensePlate;
    public  DriverStatus  status;

    public enum DriverStatus{
        AVAILABLE,    // Driver is online and ready to accept rides.
        ON_RIDE,      // Driver is currently on an active ride.
        OFFLINE,      // Driver is logged out or not available.
        BUSY,         // Driver is online but temporarily busy (e.g., break, fueling).
        SUSPENDED
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
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
