package com.cbs.Driver.Repository;

import com.cbs.Driver.Entity.Driver;
import com.cbs.Driver.dto.AvailableDriverDto;
import com.cbs.Driver.dto.DriverLoginDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDriverRepository extends JpaRepository<Driver,Long> {

    boolean existsByEmail (String email);
    Optional<Driver> findByEmail (String email);
    List<Driver> findByStatusAndIsApproved(Driver.DriverStatus status, boolean isApproved);
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    Optional<Driver> findByPhoneNumber(String phoneNumber);
}
