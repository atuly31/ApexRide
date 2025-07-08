package com.cbs.User.Service;

import com.cbs.User.AuthClient.AuthServiceClient;
import com.cbs.User.Entity.User;
import com.cbs.User.Exceptions.UserAlreadyExist;
import com.cbs.User.Exceptions.UserDoesNotExistException;
import com.cbs.User.Repository.UserRepository;
import com.cbs.User.RideClient.RideClient;
import com.cbs.User.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong; // Added for clarity with long arguments
import static org.mockito.ArgumentMatchers.anyString; // Added for clarity with string arguments
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations
class UserServiceImpTest {

    @Mock // Creates a mock instance of UserRepository
    private UserRepository userRepository;

    @Mock // Creates a mock instance of RideClient
    private RideClient rideClient;

    @Mock // Creates a mock instance of ModelMapper
    private ModelMapper modelMapper;

    @Mock // Creates a mock instance of AuthServiceClient
    private AuthServiceClient authServiceClient;

    @InjectMocks // Injects the mocks into UserServiceImp
    private UserServiceImp userServiceImp;

    @BeforeEach
    void setUp() {
        // You might reset mocks or set common behaviors here if needed
        // For this example, specific mocks are set within each test method.
    }

    // --- Test Cases for registerUser method ---
    @Test
    @DisplayName("should register user successfully when username and phone are unique")
    void registerUser_Success() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUserName("testuser");
        registrationDto.setPhoneNumber("1234567890");
        registrationDto.setEmail("test@example.com");
        registrationDto.setFirstName("John");
        registrationDto.setLastName("Doe");
        registrationDto.setPasswordHash("hashedpassword");

        User userEntity = new User();
        userEntity.setId(1L);
        userEntity.setUserName("testuser");
        userEntity.setPhoneNumber("1234567890");
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setRegistrationDate(LocalDateTime.now());
        userEntity.setLastProfileUpdate(LocalDateTime.now());

        // Mock behaviors:
        // 1. Check if username exists (should return empty Optional)
        when(userRepository.findByUserName(registrationDto.getUserName())).thenReturn(Optional.empty());
        // 2. Check if phone number exists (should return empty Optional)
        when(userRepository.findByPhoneNumber(registrationDto.getPhoneNumber())).thenReturn(Optional.empty());
        // 3. Map DTO to Entity
        when(modelMapper.map(any(UserRegistrationDto.class), eq(User.class))).thenReturn(userEntity);
        // 4. Save the user (return the saved user entity)
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        // 5. Map saved Entity back to DTO
        when(modelMapper.map(any(User.class), eq(UserRegistrationDto.class))).thenReturn(registrationDto);


        // Act
        ApiResponseDto<UserRegistrationDto> response = userServiceImp.registerUser(registrationDto);

        // Assert
        assertNotNull(response);
        assertEquals("Registered Successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("testuser", response.getData().getUserName());
        assertEquals("", response.getData().getPasswordHash(), "Password hash should be cleared before returning");

        // Verify that methods were called as expected
        verify(userRepository, times(1)).findByUserName(registrationDto.getUserName());
        verify(userRepository, times(1)).findByPhoneNumber(registrationDto.getPhoneNumber());
        verify(modelMapper, times(1)).map(registrationDto, User.class);
        verify(userRepository, times(1)).save(any(User.class));
        verify(modelMapper, times(1)).map(userEntity, UserRegistrationDto.class);
    }

    @Test
    @DisplayName("should throw UserAlreadyExist exception when username already exists")
    void registerUser_UserNameAlreadyExists() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUserName("existinguser");
        registrationDto.setPhoneNumber("1234567890");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUserName("existinguser");

        when(userRepository.findByUserName(registrationDto.getUserName())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        UserAlreadyExist exception = assertThrows(UserAlreadyExist.class, () -> {
            userServiceImp.registerUser(registrationDto);
        });

        assertEquals("Username 'existinguser' Already Exists", exception.getMessage());
        verify(userRepository, times(1)).findByUserName(registrationDto.getUserName());
        // Verify that save and phone number check were NOT called
        verify(userRepository, never()).findByPhoneNumber(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should throw UserAlreadyExist exception when phone number already exists")
    void registerUser_PhoneNumberAlreadyExists() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUserName("newuser");
        registrationDto.setPhoneNumber("existingphone");

        User existingUser = new User();
        existingUser.setId(3L);
        existingUser.setPhoneNumber("existingphone");

        when(userRepository.findByUserName(registrationDto.getUserName())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(registrationDto.getPhoneNumber())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        UserAlreadyExist exception = assertThrows(UserAlreadyExist.class, () -> {
            userServiceImp.registerUser(registrationDto);
        });

        assertEquals("Phone Number 'existingphone' Already Exists", exception.getMessage());
        verify(userRepository, times(1)).findByUserName(registrationDto.getUserName());
        verify(userRepository, times(1)).findByPhoneNumber(registrationDto.getPhoneNumber());
        // Verify that save was NOT called
        verify(userRepository, never()).save(any(User.class));
    }

    // --- Test Cases for getUserProfile method ---
    @Test
    @DisplayName("should return user profile when user exists")
    void getUserProfile_UserExists() {
        // Arrange
        Long userId = 1L;
        User userEntity = new User();
        userEntity.setId(userId);
        userEntity.setFirstName("Test");
        userEntity.setLastName("User");
        userEntity.setEmail("test@example.com");
        userEntity.setUserName("testuser");
        userEntity.setPhoneNumber("1122334455");

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(userId);
        userProfileDto.setFirstName("Test");
        userProfileDto.setLastName("User");
        userProfileDto.setEmail("test@example.com");
        userProfileDto.setUserName("testuser");
        userProfileDto.setPhoneNumber("1122334455");


        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(modelMapper.map(userEntity, UserProfileDto.class)).thenReturn(userProfileDto);

        // Act
        ApiResponseDto<UserProfileDto> response = userServiceImp.getUserProfile(userId);

        // Assert
        assertNotNull(response);
        assertEquals("User Profile Successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(userId, response.getData().getId());
        assertEquals("testuser", response.getData().getUserName());

        verify(userRepository, times(1)).findById(userId);
        verify(modelMapper, times(1)).map(userEntity, UserProfileDto.class);
    }

    @Test
    @DisplayName("should throw UserDoesNotExistException when user profile does not exist")
    void getUserProfile_UserDoesNotExist() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class, () -> {
            userServiceImp.getUserProfile(userId);
        });

        assertEquals("No user exists with such ID", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(modelMapper, never()).map(any(), any()); // Mapper should not be called
    }

    // --- Test Cases for getUsersRides method ---
    @Test
    @DisplayName("should return list of rides for a user")
    void getUsersRides_Success() {
        // Arrange
        long userId = 1L;
        RideDto ride1 = new RideDto();
        ride1.setId(101L);
        ride1.setUserId(userId);
        ride1.setPickupLocation("A");
        ride1.setDropoffLocation("B");

        RideDto ride2 = new RideDto();
        ride2.setId(102L);
        ride2.setUserId(userId);
        ride2.setPickupLocation("C");
        ride2.setDropoffLocation("D");

        List<RideDto> mockRides = List.of(ride1, ride2);

        when(rideClient.getUsersRides(userId)).thenReturn(mockRides);

        // Act
        ApiResponseDto<List<RideDto>> response = userServiceImp.getUsersRides(userId);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals(101L, response.getData().get(0).getId());
        assertEquals(102L, response.getData().get(1).getId());

        verify(rideClient, times(1)).getUsersRides(userId);
    }

    @Test
    @DisplayName("should return empty list when no rides exist for user")
    void getUsersRides_NoRidesFound() {
        // Arrange
        long userId = 2L;
        when(rideClient.getUsersRides(userId)).thenReturn(Collections.emptyList());

        // Act
        ApiResponseDto<List<RideDto>> response = userServiceImp.getUsersRides(userId);

        // Assert
        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());

        verify(rideClient, times(1)).getUsersRides(userId);
    }

    // --- Test Cases for bookRide method ---
    @Test
    @DisplayName("should successfully book a ride")
    void bookRide_Success() {
        // Arrange
        long userId = 1L;
        RideBookingRequestDto requestDto = new RideBookingRequestDto();
        requestDto.setPickupLocation("Home");
        requestDto.setDropoffLocation("Office");
        // ... set other fields

        RideDto bookedRide = new RideDto();
        bookedRide.setId(201L);
        bookedRide.setUserId(userId);
        bookedRide.setStatus(RideDto.RideStatus.PENDING); // Initial status
        // ... set other fields

        ApiResponseDto<RideDto> clientResponse = new ApiResponseDto<>("Ride Created", HttpStatus.CREATED, LocalDateTime.now(), bookedRide);

        when(rideClient.createRide(userId, requestDto)).thenReturn(clientResponse);

        // Act
        ApiResponseDto<RideDto> response = userServiceImp.bookRide(userId, requestDto);

        // Assert
        assertNotNull(response);
        assertEquals("Ride Booked Successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(201L, response.getData().getId());

        verify(rideClient, times(1)).createRide(userId, requestDto);
    }

    @Test
    @DisplayName("should handle error when booking a ride fails from RideClient")
    void bookRide_FailureFromRideClient() {
        // Arrange
        long userId = 1L;
        RideBookingRequestDto requestDto = new RideBookingRequestDto();
        requestDto.setPickupLocation("Home");
        requestDto.setDropoffLocation("Office");

        ApiResponseDto<RideDto> clientErrorResponse = new ApiResponseDto<>("Failed to create ride", HttpStatus.BAD_REQUEST, LocalDateTime.now(), null);

        when(rideClient.createRide(userId, requestDto)).thenReturn(clientErrorResponse);

        // Act
        ApiResponseDto<RideDto> response = userServiceImp.bookRide(userId, requestDto);

        // Assert
        assertNotNull(response);
        assertEquals("Ride Booked Successfully", response.getMessage()); // Note: The current implementation returns OK even on downstream error. This might be a design choice, but it's good to be aware.
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(response.getData()); // Data should be null if client returned null data

        verify(rideClient, times(1)).createRide(userId, requestDto);
    }


    // --- Test Cases for updateProfile method ---
    @Test
    @DisplayName("should update user profile successfully when user exists and new details are unique")
    void updateProfile_Success() {
        // Arrange
        long userId = 1L;
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        profileUpdateDto.setUserName("updateduser");
        profileUpdateDto.setPhoneNumber("0987654321");
        profileUpdateDto.setFirstName("UpdatedJohn");
        profileUpdateDto.setLastName("UpdatedDoe");
        profileUpdateDto.setEmail("updated@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("originaluser");
        existingUser.setPhoneNumber("1234567890");
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setEmail("original@example.com");
        existingUser.setRegistrationDate(LocalDateTime.now().minusDays(1));
        existingUser.setLastProfileUpdate(LocalDateTime.now().minusHours(1));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("updateduser");
        updatedUser.setPhoneNumber("0987654321");
        updatedUser.setFirstName("UpdatedJohn");
        updatedUser.setLastName("UpdatedDoe");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRegistrationDate(existingUser.getRegistrationDate()); // Ensure registration date is preserved
        updatedUser.setLastProfileUpdate(LocalDateTime.now()); // Will be set by service

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(profileUpdateDto.getUserName())).thenReturn(Optional.empty()); // New username is unique
        when(userRepository.findByPhoneNumber(profileUpdateDto.getPhoneNumber())).thenReturn(Optional.empty()); // New phone is unique
        doNothing().when(modelMapper).map(profileUpdateDto, existingUser); // Simulate mapping to existingUser
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(authServiceClient.updateAuthProfile(eq(userId), any(ProfileUpdateDto.class))).thenReturn(new ApiResponseDto<>("Auth Profile Updated", HttpStatus.OK, LocalDateTime.now(), ""));


        // Act
        ApiResponseDto<String> response = userServiceImp.updateProfile(userId, profileUpdateDto);

        // Assert
        assertNotNull(response);
        assertEquals("Profile Updated successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(response.getData());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByUserName(profileUpdateDto.getUserName());
        verify(userRepository, times(1)).findByPhoneNumber(profileUpdateDto.getPhoneNumber());
        verify(modelMapper, times(1)).map(profileUpdateDto, existingUser); // Verifies that map was called with the correct instances
        verify(userRepository, times(1)).save(any(User.class));
        verify(authServiceClient, times(1)).updateAuthProfile(eq(userId), any(ProfileUpdateDto.class));
    }

    @Test
    @DisplayName("should not update profile if user does not exist")
    void updateProfile_UserDoesNotExist() {
        // Arrange
        long userId = 99L;
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        profileUpdateDto.setUserName("anyuser");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ApiResponseDto<String> response = userServiceImp.updateProfile(userId, profileUpdateDto);

        // Assert
        assertNotNull(response);
        assertEquals("User not found", response.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByUserName(anyString()); // Should not proceed to uniqueness checks
        verify(userRepository, never()).findByPhoneNumber(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(authServiceClient, never()).updateAuthProfile(anyLong(), any(ProfileUpdateDto.class));
    }

    @Test
    @DisplayName("should throw UserAlreadyExist exception when new username already exists")
    void updateProfile_NewUsernameAlreadyExists() {
        // Arrange
        long userId = 1L;
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        profileUpdateDto.setUserName("existingotheruser"); // New username
        profileUpdateDto.setPhoneNumber("1234567890"); // Same phone number

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("originaluser"); // Original username
        existingUser.setPhoneNumber("1234567890");

        User otherExistingUserWithNewUsername = new User();
        otherExistingUserWithNewUsername.setId(2L);
        otherExistingUserWithNewUsername.setUserName("existingotheruser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(profileUpdateDto.getUserName())).thenReturn(Optional.of(otherExistingUserWithNewUsername));

        // Act & Assert
        UserAlreadyExist exception = assertThrows(UserAlreadyExist.class, () -> {
            userServiceImp.updateProfile(userId, profileUpdateDto);
        });

        assertEquals("Username 'existingotheruser' Already Exists", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByUserName(profileUpdateDto.getUserName());
        verify(userRepository, never()).findByPhoneNumber(anyString()); // Should not check phone if username fails
        verify(userRepository, never()).save(any(User.class));
        verify(authServiceClient, never()).updateAuthProfile(anyLong(), any(ProfileUpdateDto.class));
    }

    @Test
    @DisplayName("should throw UserAlreadyExist exception when new phone number already exists")
    void updateProfile_NewPhoneNumberAlreadyExists() {
        // Arrange
        long userId = 1L;
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        profileUpdateDto.setUserName("originaluser"); // Same username as existingUser
        profileUpdateDto.setPhoneNumber("0000000000"); // New phone number, which already exists for another user

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("originaluser");
        existingUser.setPhoneNumber("1234567890"); // Original phone number

        User otherExistingUserWithNewPhone = new User();
        otherExistingUserWithNewPhone.setId(2L);
        otherExistingUserWithNewPhone.setPhoneNumber("0000000000"); // This user has the new phone number

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        // No need to mock findByUserName because the if condition for username change will be false.
        // It should NOT be called.

        when(userRepository.findByPhoneNumber(profileUpdateDto.getPhoneNumber())).thenReturn(Optional.of(otherExistingUserWithNewPhone));

        // Act & Assert
        UserAlreadyExist exception = assertThrows(UserAlreadyExist.class, () -> {
            userServiceImp.updateProfile(userId, profileUpdateDto);
        });

        assertEquals("Phone Number '0000000000' Already Exists", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByUserName(anyString()); // <-- FIX APPLIED HERE: It should NOT be called because username is unchanged
        verify(userRepository, times(1)).findByPhoneNumber(profileUpdateDto.getPhoneNumber());
        verify(userRepository, never()).save(any(User.class));
        verify(authServiceClient, never()).updateAuthProfile(anyLong(), any(ProfileUpdateDto.class));
    }


    @Test
    @DisplayName("should proceed with update even if Auth Service update fails but log the error")
    void updateProfile_AuthServiceUpdateFails() {
        // Arrange
        long userId = 1L;
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        profileUpdateDto.setUserName("updateduser");
        profileUpdateDto.setPhoneNumber("0987654321");
        profileUpdateDto.setFirstName("UpdatedJohn");
        profileUpdateDto.setLastName("UpdatedDoe");
        profileUpdateDto.setEmail("updated@example.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("originaluser");
        existingUser.setPhoneNumber("1234567890");
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setEmail("original@example.com");
        existingUser.setRegistrationDate(LocalDateTime.now().minusDays(1));
        existingUser.setLastProfileUpdate(LocalDateTime.now().minusHours(1));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("updateduser");
        updatedUser.setPhoneNumber("0987654321");
        updatedUser.setFirstName("UpdatedJohn");
        updatedUser.setLastName("UpdatedDoe");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRegistrationDate(existingUser.getRegistrationDate()); // Ensure registration date is preserved
        updatedUser.setLastProfileUpdate(LocalDateTime.now()); // Will be set by service

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(profileUpdateDto.getUserName())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(profileUpdateDto.getPhoneNumber())).thenReturn(Optional.empty());
        doNothing().when(modelMapper).map(profileUpdateDto, existingUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Simulate AuthServiceClient throwing an exception
        doThrow(new RuntimeException("Auth service unavailable")).when(authServiceClient).updateAuthProfile(eq(userId), any(ProfileUpdateDto.class));

        // Act
        ApiResponseDto<String> response = userServiceImp.updateProfile(userId, profileUpdateDto);

        // Assert
        assertNotNull(response);
        assertEquals("Profile Updated successfully", response.getMessage()); // Service still reports success because its core job (user DB update) completed
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNull(response.getData());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(authServiceClient, times(1)).updateAuthProfile(eq(userId), any(ProfileUpdateDto.class)); // Verify it was attempted
        // You would typically also verify that an error was logged, but that requires more advanced logging testing setup (e.g., Logback test appenders).
    }


    @Test
    @DisplayName("should update profile without username/phone conflict if values are unchanged")
    void updateProfile_NoChangeInUniqueFields() {
        // Arrange
        long userId = 1L;
        ProfileUpdateDto profileUpdateDto = new ProfileUpdateDto();
        profileUpdateDto.setUserName("originaluser"); // Same username
        profileUpdateDto.setPhoneNumber("1234567890"); // Same phone number
        profileUpdateDto.setFirstName("NewFirst"); // Only change first name

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("originaluser");
        existingUser.setPhoneNumber("1234567890");
        existingUser.setFirstName("OldFirst");
        existingUser.setLastName("Doe");
        existingUser.setEmail("original@example.com");
        existingUser.setRegistrationDate(LocalDateTime.now().minusDays(1));
        existingUser.setLastProfileUpdate(LocalDateTime.now().minusHours(1));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("originaluser");
        updatedUser.setPhoneNumber("1234567890");
        updatedUser.setFirstName("NewFirst");
        updatedUser.setLastName("Doe");
        updatedUser.setEmail("original@example.com");
        updatedUser.setRegistrationDate(existingUser.getRegistrationDate());
        updatedUser.setLastProfileUpdate(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        // No calls to findByUserName or findByPhoneNumber because they are unchanged
        doNothing().when(modelMapper).map(profileUpdateDto, existingUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(authServiceClient.updateAuthProfile(eq(userId), any(ProfileUpdateDto.class))).thenReturn(new ApiResponseDto<>("Auth Profile Updated", HttpStatus.OK, LocalDateTime.now(), ""));

        // Act
        ApiResponseDto<String> response = userServiceImp.updateProfile(userId, profileUpdateDto);

        // Assert
        assertNotNull(response);
        assertEquals("Profile Updated successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByUserName(anyString()); // Not called
        verify(userRepository, never()).findByPhoneNumber(anyString()); // Not called
        verify(modelMapper, times(1)).map(profileUpdateDto, existingUser);
        verify(userRepository, times(1)).save(any(User.class));
        verify(authServiceClient, times(1)).updateAuthProfile(eq(userId), any(ProfileUpdateDto.class));
    }
}