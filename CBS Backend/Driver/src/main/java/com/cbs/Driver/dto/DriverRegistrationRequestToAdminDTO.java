package com.cbs.Driver.dto;

import jakarta.validation.constraints.NotBlank;

public class DriverRegistrationRequestToAdminDTO {
    @NotBlank(message = "Driver name cannot be empty")
    private String name;

    @NotBlank(message = "License number cannot be empty")
    private String licenseNumber; //licenseNumber

    @NotBlank(message = "Vehicle model cannot be empty")
    private String vehicleModel;

    @NotBlank(message = "Contact number cannot be empty")
    private String contactNumber;


    private Long driverId;
    // Constructors
    public DriverRegistrationRequestToAdminDTO() {

    }

    public DriverRegistrationRequestToAdminDTO(String name, String licenseNumber, String vehicleModel, String contactNumber) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.vehicleModel = vehicleModel;
        this.contactNumber = contactNumber;
    }

    public DriverRegistrationRequestToAdminDTO(String name, String licenseNumber, String vehicleModel, String contactNumber, Long driverId) {
        this.name = name;                  // Assign parameter 'name' to field 'name'
        this.licenseNumber = licenseNumber; // Assign parameter 'licenseNumber' to field 'licenseNumber'
        this.vehicleModel = vehicleModel;
        this.contactNumber = contactNumber;
        this.driverId = driverId;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }


    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
    @Override
    public String toString() {
        return "DriverRegistrationRequestDTO{" +
                "name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}
