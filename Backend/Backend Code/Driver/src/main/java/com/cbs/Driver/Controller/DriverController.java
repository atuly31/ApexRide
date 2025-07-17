package com.cbs.Driver.Controller;
import com.cbs.Driver.Entity.Driver;
import com.cbs.Driver.Service.IDriverService;
import com.cbs.Driver.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {
    @Autowired
    IDriverService driverService; //hello from Driver


    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDto<DriverProfileDto>> findDriverById(@PathVariable long id){
        return new ResponseEntity<>(driverService.getDriverById(id),HttpStatus.OK);
    }

    @PutMapping("/status/{id}")
    ResponseEntity<ApiResponseDto<Optional<Driver>>> updateDriverStatus(@PathVariable Long id , @RequestParam Driver.DriverStatus status){
        ApiResponseDto<Optional<Driver>> updateDriver = driverService.updateDriverStatus(id,status);
        return new ResponseEntity<>(updateDriver,HttpStatus.OK);

    }

    @GetMapping("/rideHistory/{id}")
    ResponseEntity<ApiResponseDto<List<RideDto>>>getDriverRideHistory(@PathVariable long id){
        return new ResponseEntity<>(driverService.getDriverRideDetails(id),HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponseDto<List<AvailableDriverDto>>> getAvailableDriver(){
        return new ResponseEntity<>(driverService.getAllAvailableDriver(),HttpStatus.OK);
    }

    @PutMapping("/complete-ride/{id}/{rideId}")
    public  ResponseEntity<String> updateRideStatus(@PathVariable Long id,@PathVariable Long rideId){
        return  new ResponseEntity<>(driverService.rideComplete(id,rideId),HttpStatus.OK);
    }

    @PatchMapping("/start-ride/{driverId}")
    public ResponseEntity<ApiResponseDto<String>> startRide(@PathVariable Long driverId){
        return new ResponseEntity<>(driverService.startRide(driverId),HttpStatus.OK);
    }

    @PutMapping("/update-approval-status")
    public ResponseEntity<Void> updateDriverApprovalStatus(@RequestBody DriverApprovalUpdateDTO updateDTO) {
        driverService.updateApprovalStatus(updateDTO);
        return ResponseEntity.ok().build();
    }

//    @PutMapping("/rating/{driverId}/{rating}")
//    public ResponseEntity<Void> addRating(@PathVariable Long driverId, @PathVariable float rating)
//    {
//        return new ResponseEntity<>(driverService.addRating(driverId,rating),HttpStatus.OK);
//    }


}
