package com.cbs.User.AuthClient;

import com.cbs.User.dto.ApiResponseDto;
import com.cbs.User.dto.ProfileUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTHSERVICE")
public interface AuthServiceClient {
    @PutMapping("/auth/updateProfile/{userId}")
    ApiResponseDto<String> updateAuthProfile(@PathVariable("userId") long userId, @RequestBody ProfileUpdateDto authProfileUpdateDto);
}
