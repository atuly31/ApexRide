package com.cbs.User.Service;

import com.cbs.User.AuthClient.AuthServiceClient;
import com.cbs.User.Entity.User;
import com.cbs.User.Exceptions.UserAlreadyExist;
import com.cbs.User.Exceptions.UserDoesNotExistException;
import com.cbs.User.RideClient.RideClient;
import com.cbs.User.dto.*;
import org.modelmapper.ModelMapper;
import com.cbs.User.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder; // Keep if PasswordEncoder is truly needed, though not directly used in presented methods
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    private final UserRepository userRepository;
    private final RideClient rideClient;
    private final ModelMapper modelMapper;
    private final AuthServiceClient authServiceClient;

    @Autowired
    public UserServiceImp(UserRepository userRepository, RideClient rideClient,
                          ModelMapper modelMapper, AuthServiceClient authServiceClient) {
        this.userRepository = userRepository;
        this.rideClient = rideClient;
        this.modelMapper = modelMapper;
        this.authServiceClient = authServiceClient;

    }

    @Override
    public ApiResponseDto<UserRegistrationDto> registerUser(UserRegistrationDto userRegistrationDto) throws UserAlreadyExist {
        logger.info("Attempting to register user with username: {}", userRegistrationDto.getUserName());
        logger.debug("User registration DTO: {}", userRegistrationDto);

        // --- Pre-checks for uniqueness in User Service ---
        if (userRepository.findByUserName(userRegistrationDto.getUserName()).isPresent()) {
            logger.warn("Registration failed: Username '{}' already exists.", userRegistrationDto.getUserName());
            throw new UserAlreadyExist("Username '" + userRegistrationDto.getUserName() + "' Already Exists");
        }
        if (userRepository.findByPhoneNumber(userRegistrationDto.getPhoneNumber()).isPresent()) {
            logger.warn("Registration failed: Phone Number '{}' already exists.", userRegistrationDto.getPhoneNumber());
            throw new UserAlreadyExist("Phone Number '" + userRegistrationDto.getPhoneNumber() + "' Already Exists");
        }
        logger.debug("Username and Phone Number are unique, proceeding with registration.");

        User user = modelMapper.map(userRegistrationDto, User.class);
        user.setLastProfileUpdate(LocalDateTime.now());
        user.setRegistrationDate(LocalDateTime.now());
        logger.debug("Mapped user entity before saving: {}", user);

        User savedUser = userRepository.save(user); // Saves the user details to DB
        logger.info("User saved successfully with ID: {}", savedUser.getId());

        // Send welcome email (consider making this async or robust with retry)
        // Re-enable and inject EmailServiceImp if needed
        /*
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("Username", userRegistrationDto.getUserName());
        templateVariables.put("email", userRegistrationDto.getEmail());
        String subject = "Welcome to ApexRide!";
        try {
            emailService.sendHtmlMail(userRegistrationDto.getEmail(), subject, "registration-welcome", templateVariables);
            logger.info("Welcome email sent to: {}", userRegistrationDto.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}: {}", userRegistrationDto.getEmail(), e.getMessage(), e);
            // You might want to queue this email for a retry later
        }
        */

        UserRegistrationDto userSavedDto = modelMapper.map(savedUser, UserRegistrationDto.class);
        userSavedDto.setPasswordHash(""); // Always clear sensitive info before returning
        return new ApiResponseDto<>("Registered Successfully", HttpStatus.OK, LocalDateTime.now(), userSavedDto);
    }

    @Override
    public ApiResponseDto<UserProfileDto> getUserProfile(Long id) {
        logger.info("Fetching user profile for ID: {}", id);
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            logger.warn("No user exists with ID: {} for profile fetch.", id);
            throw new UserDoesNotExistException("No user exists with such ID");
        }
        UserProfileDto userDto = modelMapper.map(user.get(), UserProfileDto.class);
        logger.info("User profile successfully fetched for ID: {}", id);
        return new ApiResponseDto<>("User Profile Successfully", HttpStatus.OK, LocalDateTime.now(), userDto);
    }

    @Override
    public ApiResponseDto<List<RideDto>> getUsersRides(long id) {
        logger.info("Fetching rides for user ID: {}", id);
        List<RideDto> rideDetails = rideClient.getUsersRides(id);
        logger.info("Fetched {} rides for user ID: {}", rideDetails.size(), id);
        return new ApiResponseDto<>("Success", HttpStatus.OK, LocalDateTime.now(), rideDetails);
    }

    @Override
    public ApiResponseDto<RideDto> bookRide(long userID, RideBookingRequestDto requestDto) {
        logger.info("User ID: {} is attempting to book a ride.", userID);
        logger.debug("Ride booking request DTO: {}", requestDto);
        ApiResponseDto<RideDto> rideDtoApiResponseDto = rideClient.createRide(userID, requestDto);
        if (rideDtoApiResponseDto.getStatus() == HttpStatus.OK || rideDtoApiResponseDto.getStatus() == HttpStatus.CREATED) {
            logger.info("Ride booked successfully for user ID: {} with ride ID: {}", userID, rideDtoApiResponseDto.getData() != null ? rideDtoApiResponseDto.getData().getId() : "N/A");
        } else {
            logger.error("Failed to book ride for user ID: {}. Status: {}, Message: {}", userID, rideDtoApiResponseDto.getStatus(), rideDtoApiResponseDto.getMessage());
        }
        return new ApiResponseDto<>("Ride Booked Successfully", HttpStatus.OK, LocalDateTime.now(), rideDtoApiResponseDto.getData());
    }

    @Override
    public ApiResponseDto<String> updateProfile(long userId, ProfileUpdateDto profileUpdateDto) {
        logger.info("Attempting to update profile for user ID: {}", userId);
        logger.debug("ProfileUpdateDto: {}", profileUpdateDto);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            logger.warn("User with ID {} not found for profile update.", userId);
            return new ApiResponseDto<>("User not found", HttpStatus.NOT_FOUND, LocalDateTime.now(), null);
        }

        User existingUser = optionalUser.get();

        // Check for uniqueness of updated fields if they are changed
        if (!existingUser.getUserName().equals(profileUpdateDto.getUserName())) {
            if (userRepository.findByUserName(profileUpdateDto.getUserName()).isPresent()) {
                logger.warn("Profile update failed for user ID {}: New username '{}' already exists.", userId, profileUpdateDto.getUserName());
                throw new UserAlreadyExist("Username '" + profileUpdateDto.getUserName() + "' Already Exists");
            }
        }
        if (!existingUser.getPhoneNumber().equals(profileUpdateDto.getPhoneNumber())) {
            if (userRepository.findByPhoneNumber(profileUpdateDto.getPhoneNumber()).isPresent()) {
                logger.warn("Profile update failed for user ID {}: New phone number '{}' already exists.", userId, profileUpdateDto.getPhoneNumber());
                throw new UserAlreadyExist("Phone Number '" + profileUpdateDto.getPhoneNumber() + "' Already Exists");
            }
        }


        modelMapper.map(profileUpdateDto, existingUser);
        existingUser.setLastProfileUpdate(LocalDateTime.now());
        // Ensure the ID is not accidentally overwritten by modelMapper if it's in DTO
        existingUser.setId(userId);
        userRepository.save(existingUser);
        logger.info("User profile updated in User Service for ID: {}", userId);

        try {
            // Update profile in Auth Service (assuming email and username are updated via authServiceClient)
            // Note: AuthServiceClient's updateAuthProfile expects an AuthProfileUpdateDto.
            // You need to map profileUpdateDto to AuthProfileUpdateDto if they are different,
            // or ensure ProfileUpdateDto contains necessary fields for AuthProfileUpdateDto.
            // Assuming ProfileUpdateDto maps directly or contains all fields needed for AuthProfileUpdateDto
            authServiceClient.updateAuthProfile(userId, profileUpdateDto);
            logger.info("User profile updated in Auth Service for ID: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to update profile in Auth Service for user ID {}: {}", userId, e.getMessage(), e);
            // Decide how to handle this. You might want to return an error,
            // or just log it and proceed if the Auth Service update is not critical for the User Service's response.
            // For now, it proceeds and logs.
        }

        return new ApiResponseDto<>("Profile Updated successfully", HttpStatus.OK, LocalDateTime.now(), null);
    }
}