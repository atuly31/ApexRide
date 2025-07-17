package com.cbs.Driver.Controller;

import com.cbs.Driver.Service.IDriverService;
import com.cbs.Driver.dto.ApiResponseDto;
import com.cbs.Driver.dto.DriverRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
public class PublicController {
    @Autowired
    IDriverService driverService; //hello from Driver

    @PostMapping("/register-details")
    ResponseEntity<ApiResponseDto<DriverRegistrationDto>> addDriver(@RequestBody DriverRegistrationDto driverRegistrationDto){
        return new ResponseEntity<>(driverService.registerDriver(driverRegistrationDto), HttpStatus.CREATED);
    }


}
