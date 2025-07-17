package com.cbs.AuthService.Client;

import com.cbs.AuthService.AuthDto.ApiResponseDto;
import com.cbs.AuthService.AuthDto.DriverRegistrationDto;
import com.cbs.AuthService.AuthDto.UserRegistrationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("Driver")
public interface DriverClient {
    @PostMapping("/api/v1/drivers/register-details")
    ApiResponseDto<DriverRegistrationDto> addDriver(@RequestBody DriverRegistrationDto userRegistrationDto);

}
