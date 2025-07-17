package com.cbs.Admin.AdminDTO;

import jakarta.validation.constraints.NotBlank;

public class DriverRegistrationRequestDTO {


        @NotBlank(message = "Driver name cannot be empty")
        private String name;

        @NotBlank(message = "License number cannot be empty")
        private String licenseNumber;

        @NotBlank(message = "Vehicle model cannot be empty")
        private String vehicleModel;

        @NotBlank(message = "Contact number cannot be empty")
        private String contactNumber;


    private Long driverId;
        // Constructors
        public DriverRegistrationRequestDTO() {
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
