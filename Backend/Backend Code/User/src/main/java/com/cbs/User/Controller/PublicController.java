package com.cbs.User.Controller;

import com.cbs.User.Exceptions.IncorrectPasswordException;
import com.cbs.User.Exceptions.UserDoesNotExistException;
//import com.cbs.User.Service.IAuthService;
import com.cbs.User.Service.UserService;
import com.cbs.User.dto.ApiResponseDto;
import com.cbs.User.dto.JwtResponse;
import com.cbs.User.dto.UserLoginDto;
import com.cbs.User.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class PublicController {

    @Autowired
    UserService userService;
    @PostMapping ("/register-details")
    ResponseEntity<ApiResponseDto<UserRegistrationDto>> addUser(@RequestBody UserRegistrationDto userRegistrationDto){
        return new ResponseEntity<>(userService.registerUser(userRegistrationDto), HttpStatus.CREATED);
    }
}
