package com.cbs.User.Service;


import com.cbs.User.Exceptions.IncorrectPasswordException;
import com.cbs.User.Exceptions.UserDoesNotExistException;
import com.cbs.User.dto.*;

import java.util.List;


public interface UserService {
      ApiResponseDto<UserRegistrationDto> registerUser(UserRegistrationDto userRegistrationDto);
      ApiResponseDto<UserProfileDto> getUserProfile(Long id);
      ApiResponseDto<List<RideDto>> getUsersRides(long id);
      ApiResponseDto<RideDto> bookRide(long userID, RideBookingRequestDto requestDto);
      ApiResponseDto<String> updateProfile(long userId,ProfileUpdateDto profileUpdateDto);

}
