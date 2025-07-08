package com.cbs.Driver.Service;

import com.cbs.Driver.Entity.Driver;
import com.cbs.Driver.dto.*;

import java.util.List;
import java.util.Optional;

public interface IDriverService {

    ApiResponseDto<DriverRegistrationDto> registerDriver(DriverRegistrationDto driverRegistrationDto);
    ApiResponseDto<DriverProfileDto> getDriverById (Long id);
    boolean updateApprovalStatus(DriverApprovalUpdateDTO updateDTO);
    ApiResponseDto<Optional<Driver>> updateDriverStatus(Long id , Driver.DriverStatus status);
    ApiResponseDto<List<AvailableDriverDto>> getAllAvailableDriver();
    String rideComplete(Long id, Long rideId);
    ApiResponseDto<List<RideDto>> getDriverRideDetails(long driverId);
    ApiResponseDto<String> startRide(Long driverId);
//    Void addRating(Long driverId, float rating);
}
