package com.cbs.AuthService.Service;

import com.cbs.AuthService.AuthDto.ApiResponseDto;
import com.cbs.AuthService.AuthDto.AuthProfileUpdateDto;
import com.cbs.AuthService.AuthDto.AuthRequest;
import com.cbs.AuthService.AuthDto.PasswordDto;
import com.cbs.AuthService.JWT.JwtResponse;
import org.springframework.stereotype.Component;


public interface IAuthService {
     JwtResponse loginUser(AuthRequest authRequest);
     ApiResponseDto<String> changePassword(long userId, PasswordDto passwordDto);
}
