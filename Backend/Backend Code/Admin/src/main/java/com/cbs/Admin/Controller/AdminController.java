package com.cbs.Admin.Controller;

import com.cbs.Admin.AdminDTO.AdminApprovalRequestDTO;
import com.cbs.Admin.AdminDTO.DriverDTO;
import com.cbs.Admin.AdminDTO.DriverRegistrationRequestDTO;
import com.cbs.Admin.Exception.DriverNotFoundException;
import com.cbs.Admin.Exception.DuplicateDriverRegistrationException;
import com.cbs.Admin.Service.IAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final IAdminService adminService;

    @Autowired
    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }


    @PostMapping("/receive-registration")
    public ResponseEntity<DriverDTO> receiveDriverRegistrationForApproval(@Valid @RequestBody DriverRegistrationRequestDTO requestDTO) {
        try {
            DriverDTO recordedDriver = adminService.recordNewDriverForApproval(requestDTO);
            return new ResponseEntity<>(recordedDriver, HttpStatus.CREATED);
        } catch (DuplicateDriverRegistrationException e) {
            // The @ResponseStatus on DuplicateDriverRegistrationException handles the 409
            throw e;
        }
    }


    @GetMapping("/unapproved")
    public ResponseEntity<List<DriverDTO>> getUnapprovedDrivers() {
        List<DriverDTO> unapprovedDrivers = adminService.getUnapprovedDrivers();
        return ResponseEntity.ok(unapprovedDrivers);
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long driverId) {
        try {
            DriverDTO driver = adminService.getDriverById(driverId);
            return ResponseEntity.ok(driver);
        } catch (DriverNotFoundException e) {
            throw e;
        }
    }


    @PutMapping("/{driverId}/process-approval")
    public ResponseEntity<DriverDTO> processDriverApproval(
            @PathVariable Long driverId,
            @Valid @RequestBody AdminApprovalRequestDTO approvalDTO) {
        try {
            DriverDTO updatedDriver = adminService.processDriverApproval(driverId, approvalDTO);
            return ResponseEntity.ok(updatedDriver);
        } catch (DriverNotFoundException e)  {
            throw e;
        }
    }
}
