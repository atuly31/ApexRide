package com.cbs.AuthService.Service;

import com.cbs.AuthService.AuthDto.*;
import com.cbs.AuthService.Entity.AuthEntity;


public interface RegistrationService {
    ApiResponseDto<AuthEntity> registerUser(UserRegistrationDto registrationDto);
    ApiResponseDto<AuthEntity> registerDriver(DriverRegistrationDto registrationDto);
    AuthProfileUpdateDto updateUserProfile(long userId, AuthProfileUpdateDto authProfileUpdateDto) throws Exception;

}
