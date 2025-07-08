package com.cbs.Driver.dto;

import java.time.LocalDateTime;

public class DriverApprovalUpdateDTO {
    private String licenseNumber; // Key to identify the driver
    private boolean approved;
    private LocalDateTime approvalDate;
    private String approvedByAdminId;
    private String remarks;

    public String getApprovedByAdminId() {
        return approvedByAdminId;
    }

    public void setApprovedByAdminId(String approvedByAdminId) {
        this.approvedByAdminId = approvedByAdminId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


}
