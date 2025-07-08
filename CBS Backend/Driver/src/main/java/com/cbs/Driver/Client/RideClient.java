package com.cbs.Driver.Client;

import com.cbs.Driver.dto.ApiResponseDto;
import com.cbs.Driver.dto.RideDto;
import com.cbs.Driver.dto.currentDriverDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient(name="RIDE")
public interface RideClient {
    @PutMapping("/api/v1/rides/update-rideStatus")
    public ApiResponseDto<String> updateRideStatus(@RequestParam Long id , @RequestParam RideDto.RideStatus status);

    @GetMapping("/api/v1/rides/drivers/{id}")
    List<RideDto> getDriverRides(@PathVariable("id") long id);

    @GetMapping("api/v1/rides/latest-assigned-by-driver/{driverId}")
    Optional<currentDriverDto> getLatestAssignedRideForDriver(@PathVariable Long driverId);
}
