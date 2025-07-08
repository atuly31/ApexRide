package com.cbs.AuthService.Client;

import com.cbs.AuthService.AuthDto.ApiResponseDto;
import com.cbs.AuthService.AuthDto.UserRegistrationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "User")

public interface UserClient {

    @PostMapping("/api/v1/users/register-details")
    ApiResponseDto<UserRegistrationDto> addUser(@RequestBody UserRegistrationDto userRegistrationDto);



}