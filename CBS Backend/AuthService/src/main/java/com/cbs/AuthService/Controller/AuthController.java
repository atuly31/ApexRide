package com.cbs.AuthService.Controller;

import com.cbs.AuthService.AuthDto.*;
import com.cbs.AuthService.Entity.AuthEntity;
import com.cbs.AuthService.JWT.JwtResponse;
import com.cbs.AuthService.Service.IAuthService;
import com.cbs.AuthService.Service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    RegistrationService registrationService;
    @Autowired
    IAuthService authService; //Added

    @PostMapping("/register/user")
    ResponseEntity<ApiResponseDto<AuthEntity>> registerUser(@RequestBody UserRegistrationDto registrationDto){
        return new ResponseEntity<>(registrationService.registerUser(registrationDto), HttpStatus.OK);
    }

    @PostMapping("/register/driver")
    ResponseEntity<ApiResponseDto<AuthEntity>> registerDriver(@RequestBody DriverRegistrationDto registrationDto){
        return new ResponseEntity<>(registrationService.registerDriver(registrationDto), HttpStatus.OK);
    }

    @PostMapping("/login")
    ResponseEntity<JwtResponse> login(@RequestBody AuthRequest authRequest){
        return new ResponseEntity<>(authService.loginUser(authRequest),HttpStatus.OK);
    }
    @PostMapping("/update/password/{userId}")
    public ResponseEntity<ApiResponseDto<String>> updatePassword( @PathVariable  long userId, @RequestBody PasswordDto passwordDto){
        ApiResponseDto<String> response = authService.changePassword(userId,passwordDto);
        return  new ResponseEntity<>(response,response.getStatus());
    }
    @PutMapping("/updateProfile/{userId}")
    public ResponseEntity<ApiResponseDto<AuthProfileUpdateDto>> updateAuthUserProfile(@PathVariable long userId, @RequestBody AuthProfileUpdateDto authProfileUpdateDto) {
        try {
           AuthProfileUpdateDto updatedProfile =  registrationService.updateUserProfile(userId, authProfileUpdateDto);
            return new ResponseEntity<>(new ApiResponseDto<>("Auth profile updated successfully", HttpStatus.OK, LocalDateTime.now(), updatedProfile), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponseDto<>("Failed to update auth profile: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
