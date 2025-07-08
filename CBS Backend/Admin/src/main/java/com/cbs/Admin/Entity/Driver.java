package com.cbs.Admin.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entity representing a Driver in the database of the Admin Microservice
// This entity primarily tracks drivers awaiting or having received approval.
@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Long driverId;
    
    @Column(nullable = false, unique = true) // licenseNumber might be a good unique identifier across services
    private String licenseNumber;

    private String vehicleModel;

    private String contactNumber;

    @Column(nullable = false)
    private boolean isApproved; // This field indicates if the driver's profile is approved by an admin

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate; // When the driver record was received by the Admin service

    private LocalDateTime approvalDate; // When the driver was approved by an admin
    private String approvedByAdminId; // Who approved the driver

    // Constructors
    public Driver() {
        this.isApproved = false; // Default to not approved when received for the first time
        this.registrationDate = LocalDateTime.now(); // Set date when admin service records it
    }



    public Driver(String name, String licenseNumber, String vehicleModel, String contactNumber, Long driverId) {
        this(); // Call default constructor to initialize isApproved and registrationDate
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.vehicleModel = vehicleModel;
        this.contactNumber = contactNumber;
        this.driverId = driverId; // Initialize driverId here
    }

    // Getters
    public Long getId() {
        return id;
    }

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

    public boolean isApproved() {
        return isApproved;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public String getApprovedByAdminId() {
        return approvedByAdminId;
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
        isApproved = approved;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public void setApprovedByAdminId(String approvedByAdminId) {
        this.approvedByAdminId = approvedByAdminId;
    }
    
    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }


    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", isApproved=" + isApproved +
                ", registrationDate=" + registrationDate +
                ", approvalDate=" + approvalDate +
                ", approvedByAdminId='" + approvedByAdminId + '\'' +
                '}';
    }
}