package com.cbs.Admin.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "AdminTable")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long driverId; // ID of the driver whose request was processed

    @Column(nullable = false)
    private String adminId; // ID of the admin who performed the action

    @Column(nullable = false)
    private boolean approvalStatus; // true for approved, false for rejected

    @Column(nullable = false)
    private LocalDateTime approvalTimestamp;

    private String remarks; // Optional remarks

    // Constructors
    public Admin() {
        this.approvalTimestamp = LocalDateTime.now();
    }

    public Admin(Long driverId, String adminId, boolean approvalStatus, String remarks) {
        this();
        this.driverId = driverId;
        this.adminId = adminId;
        this.approvalStatus = approvalStatus;
        this.remarks = remarks;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public String getAdminId() {
        return adminId;
    }

    public boolean isApprovalStatus() {
        return approvalStatus;
    }

    public LocalDateTime getApprovalTimestamp() {
        return approvalTimestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setApprovalStatus(boolean approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void setApprovalTimestamp(LocalDateTime approvalTimestamp) {
        this.approvalTimestamp = approvalTimestamp;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "AdminApprovalEntry{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", adminId='" + adminId + '\'' +
                ", approvalStatus=" + approvalStatus +
                ", approvalTimestamp=" + approvalTimestamp +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
