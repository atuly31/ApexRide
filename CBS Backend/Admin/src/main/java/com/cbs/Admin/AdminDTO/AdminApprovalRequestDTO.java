package com.cbs.Admin.AdminDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin("*")
public class AdminApprovalRequestDTO {
    @NotNull(message = "Approval status cannot be null")
    private Boolean approved; // true for approved, false for rejected

    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    private String remarks; // Optional remarks from the admin

    @NotBlank(message = "Admin ID cannot be empty")
    private String adminId; // ID of the admin performing the action

    // Constructors
    public AdminApprovalRequestDTO() {
    }

    public AdminApprovalRequestDTO(Boolean approved, String remarks, String adminId) {
        this.approved = approved;
        this.remarks = remarks;
        this.adminId = adminId;
    }

    // Getters
    public Boolean getApproved() {
        return approved;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getAdminId() {
        return adminId;
    }

    // Setters
    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return "AdminApprovalRequestDTO{" +
                "approved=" + approved +
                ", remarks='" + remarks + '\'' +
                ", adminId='" + adminId + '\'' +
                '}';
    }
}
