package com.cbs.Payment.Client;

import com.cbs.Payment.dto.ApiResponseDto;
import com.cbs.Payment.dto.DriverStatus;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping; // Changed from PatchMapping
import org.springframework.web.bind.annotation.RequestParam;

// The name "DRIVER" must match the 'spring.application.name' of your Driver Microservice
@FeignClient(name = "DRIVER")
public interface DriverClient {

    @PutMapping("/api/v1/drivers/status/{id}")
    void updateDriverStatus(@PathVariable Long id, @RequestParam DriverStatus status);
}