package com.cbs.Admin.AdminDTO;

public class DriverDTO {
    private Long id;
    private String name;
    private String licenseNumber;
    private String vehicleModel;
    private String contactNumber;
    private boolean approved;
    private Long driverID;

    // Constructors
    public DriverDTO() {
    }

    public DriverDTO(Long id, String name, String licenseNumber, String vehicleModel, String contactNumber, boolean approved) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.vehicleModel = vehicleModel;
        this.contactNumber = contactNumber;
        this.approved = approved;
    }

    public DriverDTO(Long id, String name, String licenseNumber, String vehicleModel, String contactNumber, boolean approved, Long driverId) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.vehicleModel = vehicleModel;
        this.contactNumber = contactNumber;
        this.approved = approved;
        this.driverID = driverId;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Long getDriverID() {
        return driverID;
    }
    public void setDriverID(Long driverID) {
        this.driverID = driverID;
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

    public boolean isApproved() {
        return approved;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

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

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "DriverDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", approved=" + approved +
                '}';
    }
}
