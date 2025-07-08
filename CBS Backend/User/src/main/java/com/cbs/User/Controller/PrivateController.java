package com.cbs.User.Controller;

import com.cbs.User.Exceptions.UserDoesNotExistException;
import com.cbs.User.Service.UserService;
import com.cbs.User.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class PrivateController {
    @Autowired
    UserService userService;


    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDto<UserProfileDto>> getUserById(@PathVariable Long id) throws UserDoesNotExistException{
        return new ResponseEntity<>(userService.getUserProfile(id),HttpStatus.OK);
    }

    @GetMapping("/rides/{id}")
    public ResponseEntity<ApiResponseDto<List<RideDto>>> getUserRides(@PathVariable  long id){
        return  new ResponseEntity<>(userService.getUsersRides(id),HttpStatus.OK);
    }

    @PostMapping("/book-ride/{userID}")
    public ResponseEntity<ApiResponseDto<RideDto>> getUserBookRide(@PathVariable long userID, @RequestBody RideBookingRequestDto requestDto){
        return new ResponseEntity<>(userService.bookRide(userID,requestDto),HttpStatus.OK);
    }

    @PutMapping("/updateProfile/{userId}")
    public ResponseEntity<ApiResponseDto<String>> updateUserProfile(@PathVariable long userId ,@RequestBody ProfileUpdateDto profileUpdateDto){
        return new ResponseEntity<>(userService.updateProfile(userId, profileUpdateDto),HttpStatus.OK);
    }



}
