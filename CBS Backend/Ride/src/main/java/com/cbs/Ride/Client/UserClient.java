package com.cbs.Ride.Client;

import com.cbs.Ride.Dto.ApiResponseDto;
import com.cbs.Ride.Dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="USER")
public interface UserClient {
    @GetMapping("/api/v1/users/{id}")
    ApiResponseDto<UserDto> getUserById(@PathVariable("id") Long id);
}
