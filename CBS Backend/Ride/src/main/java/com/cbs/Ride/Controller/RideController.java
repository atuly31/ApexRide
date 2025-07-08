package com.cbs.Ride.Controller;

import com.cbs.Ride.Dto.ApiResponseDto;
import com.cbs.Ride.Dto.RideBookingRequestDto;
import com.cbs.Ride.Dto.RideDto;
import com.cbs.Ride.Dto.RideStatusUpdateRequest;
import com.cbs.Ride.Entity.Rides;
import com.cbs.Ride.Service.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
public class RideController {
    @Autowired
    IRideService rideService;

    @PostMapping("/book-ride/{userID}")
    public ResponseEntity<ApiResponseDto<RideDto>> createRide(@PathVariable long userID, @RequestBody RideBookingRequestDto requestDto) {
        return new ResponseEntity<>(rideService.requestRide(userID, requestDto), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<RideDto>> getUsersRides(@PathVariable long id) {
        return new ResponseEntity<List<RideDto>>(rideService.getUsersRides(id), HttpStatus.OK);
    }

    @GetMapping("/drivers/{id}")
    ResponseEntity<List<RideDto>> getDriverRides(@PathVariable long id) {
        List<RideDto> rideDtoList = rideService.getDriverRidesDetails(id);
        System.out.println("Inside The Controller");
        System.out.println(rideDtoList);
        return new ResponseEntity<>(rideDtoList, HttpStatus.OK);
    }

    @PutMapping("/update-rideStatus")
    public ResponseEntity<ApiResponseDto<String>> updateRideStatus(@RequestParam Long id, Rides.RideStatus status) {
        return new ResponseEntity<>(rideService.updateRideStatus(id, status), HttpStatus.OK);
    }

    @PatchMapping("/cancel-ride/{rideId}")
    public ResponseEntity<ApiResponseDto<String>> cancelRide(@PathVariable long rideId, @RequestBody RideStatusUpdateRequest updateRequest) {
        return new ResponseEntity<>(rideService.cancelRide(rideId, updateRequest), HttpStatus.OK);
    }

    @GetMapping("/latest-assigned-by-driver/{driverId}")
    public ResponseEntity<?> getLatestAssignedRideForDriver(@PathVariable Long driverId) {

        Rides latestRide = rideService.getLatestAssignedRideForDriver(driverId);
        return ResponseEntity.ok(latestRide);

    }
    @GetMapping("/latest-assigned-by-user/{userId}")
    public ResponseEntity<?> getLatestAssignedRideForUser(@PathVariable Long userId) {

        Rides latestRide = rideService.getLatestAssignedRideForUser(userId);
        return ResponseEntity.ok(latestRide);

    }

    @PutMapping("/rating/{rideId}/{rating}")
    public ResponseEntity<Void> addRating(@PathVariable Long rideId, @PathVariable int rating)
    {
        return new ResponseEntity<>(rideService.addRating(rideId,rating),HttpStatus.OK);
    }


}
