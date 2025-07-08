package com.cbs.Payment.Client; // Updated package name

import com.cbs.Payment.dto.ApiResponseDto; // Assuming ApiResponseDto is in com.cbs.Payment.dto
import com.cbs.Payment.dto.RideStatus; // This will be copied/defined soon in com.cbs.Payment.dto
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "Ride", path = "/api/v1/rides") // "Ride" is the name of Ride MS in Eureka
@FeignClient(url = "http://localhost:8082", name="Ride") // "Ride" is the name of Ride MS in Eureka
public interface RideFeignClient {

    /**
     * Updates the status of a ride in the Ride Microservice.
     * This method maps to the PATCH /api/v1/rides/status/{rideId} endpoint in the Ride MS.
     *
     * @param rideId The ID of the ride.
     * @param newStatus The new status to set for the ride (e.g., COMPLETED).
     * @return ApiResponseDto containing a String message.
     */
    @PutMapping("/api/v1/rides/status/{rideId}")
    ApiResponseDto<String> updateRideStatus(
            @PathVariable("rideId") Long rideId,
            @RequestParam("newStatus") RideStatus newStatus
    );
}