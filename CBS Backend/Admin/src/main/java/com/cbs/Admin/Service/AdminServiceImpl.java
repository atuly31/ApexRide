package com.cbs.Admin.Service;

import com.cbs.Admin.AdminDTO.AdminApprovalRequestDTO;
import com.cbs.Admin.AdminDTO.DriverApprovalUpdateDTO;
import com.cbs.Admin.AdminDTO.DriverDTO;
import com.cbs.Admin.AdminDTO.DriverRegistrationRequestDTO;
import com.cbs.Admin.Client.DriverServiceFeignClient;
import com.cbs.Admin.Entity.Admin;
import com.cbs.Admin.Entity.Driver;
import com.cbs.Admin.Exception.DriverNotFoundException;
import com.cbs.Admin.Exception.DuplicateDriverRegistrationException;
import com.cbs.Admin.Repository.AdminApprovalEntryRepository;
import com.cbs.Admin.Repository.DriverRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements  IAdminService{
    private final DriverRepository driverRepository;
    private final AdminApprovalEntryRepository adminApprovalEntryRepository;
    private final DriverServiceFeignClient driverServiceFeignClient;
    @Autowired
    public AdminServiceImpl(DriverRepository driverRepository, AdminApprovalEntryRepository adminApprovalEntryRepository,DriverServiceFeignClient driverServiceFeignClient) {
        this.driverRepository = driverRepository;
        this.adminApprovalEntryRepository = adminApprovalEntryRepository;
        this.driverServiceFeignClient = driverServiceFeignClient;
    }


@Transactional
public DriverDTO recordNewDriverForApproval(DriverRegistrationRequestDTO requestDTO)  {
    System.out.println("Admin Service - Received request for licenseNumber: " + requestDTO.getLicenseNumber());

    Optional<Driver> existingDriverOptional = driverRepository.findByLicenseNumber(requestDTO.getLicenseNumber());
    if (existingDriverOptional.isPresent()) {
        System.err.println("Admin Service - Driver with license number " + requestDTO.getLicenseNumber() + " already exists. Throwing conflict.");
        throw new DuplicateDriverRegistrationException("Driver with license number " + requestDTO.getLicenseNumber() + " already exists in Admin records.");
    }

    Driver driver = new Driver(
            requestDTO.getName(),
            requestDTO.getLicenseNumber(),
            requestDTO.getVehicleModel(),
            requestDTO.getContactNumber(),
            requestDTO.getDriverId()
    );

    try {
        Driver savedDriver = driverRepository.save(driver);
        System.out.println("Admin Service - Driver saved successfully with ID: " + savedDriver.getId());
        return convertToDriverDTO(savedDriver);

    } catch (Exception e) {
        System.err.println("Admin Service - Unexpected error saving driver: " + e.getMessage());
        throw new RuntimeException("Error processing driver for approval: " + e.getMessage(), e);
    }
}


    @Transactional
    public DriverDTO processDriverApproval(Long driverId, AdminApprovalRequestDTO approvalDTO) throws DriverNotFoundException {

        Optional<Driver> driverOptional = driverRepository.findByDriverId(driverId);

        if(driverOptional.isEmpty()){
           throw new DriverNotFoundException("Driver with ID " + driverId + " not found.");
        }
        Driver driver = driverOptional.get();
        driver.setApproved(approvalDTO.getApproved());
        if (approvalDTO.getApproved()) {
            driver.setApprovalDate(LocalDateTime.now());
            driver.setApprovedByAdminId(approvalDTO.getAdminId());
        } else {
            driver.setApprovalDate(null);
            driver.setApprovedByAdminId(null);
        }

        Driver updatedDriver = driverRepository.save(driver);

        Admin approvalEntry = new Admin(
                driverId,
                approvalDTO.getAdminId(),
                approvalDTO.getApproved(),
                approvalDTO.getRemarks()
        );

        adminApprovalEntryRepository.save(approvalEntry);

        DriverApprovalUpdateDTO updateToDriverService = new DriverApprovalUpdateDTO(
                updatedDriver.getLicenseNumber(), // Use license number as the identifier
                updatedDriver.isApproved(),
                updatedDriver.getApprovedByAdminId(),
                approvalDTO.getRemarks(),
                updatedDriver.getApprovalDate()
        );
        driverServiceFeignClient.updateDriverApprovalStatus(updateToDriverService);
        return convertToDriverDTO(updatedDriver);
    }

    public List<DriverDTO> getUnapprovedDrivers() {
        return driverRepository.findByIsApprovedFalse().stream()
                .map(this::convertToDriverDTO)
                .collect(Collectors.toList());
    }




    public DriverDTO getDriverById(Long driverId) throws DriverNotFoundException {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver with ID " + driverId + " not found."));
        return convertToDriverDTO(driver);
    }


    private DriverDTO convertToDriverDTO(Driver driver) {
        return new DriverDTO(
                driver.getId(),
                driver.getName(),
                driver.getLicenseNumber(),
                driver.getVehicleModel(),
                driver.getContactNumber(),
                driver.isApproved(),
                driver.getDriverId()

        );
    }
}
