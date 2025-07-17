package com.cbs.Ride.Service;

import com.cbs.Ride.Dto.ApiResponseDto;
import com.cbs.Ride.Dto.RideBookingRequestDto;
import com.cbs.Ride.Dto.RideDto;
import com.cbs.Ride.Dto.RideStatusUpdateRequest;
import com.cbs.Ride.Entity.Rides;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IRideService {
    ApiResponseDto<RideDto> requestRide(long userID, RideBookingRequestDto requestDto);
    List<RideDto> getUsersRides(long userId);
    ApiResponseDto<String> updateRideStatus(Long id,Rides.RideStatus status);
    List<RideDto> getDriverRidesDetails(long driverId);
    ApiResponseDto<String> cancelRide(long id,RideStatusUpdateRequest updateRequest);
    Rides getLatestAssignedRideForDriver(Long driverId);
    Void addRating(Long rideId, int rating);
    Rides getLatestAssignedRideForUser(Long userId);

}
