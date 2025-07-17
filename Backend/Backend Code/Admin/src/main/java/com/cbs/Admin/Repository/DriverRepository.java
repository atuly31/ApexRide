package com.cbs.Admin.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import  com.cbs.Admin.Entity.Driver;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByIsApprovedFalse();
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    Optional<Driver> findByDriverId(Long driverId);

}