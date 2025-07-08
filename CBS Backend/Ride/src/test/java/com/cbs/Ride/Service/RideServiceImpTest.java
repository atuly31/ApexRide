package com.cbs.Ride.Service;

import com.cbs.Ride.Client.DriverClient;
import com.cbs.Ride.Client.UserClient;
import com.cbs.Ride.Dto.*;
import com.cbs.Ride.Entity.Rides;
import com.cbs.Ride.Exception.RideNotFoundException;
import com.cbs.Ride.Repository.RideRepository;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito annotations
class RideServiceImpTest {

    @Mock
    private RideRepository rideRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private DriverClient driverClient;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks // Injects the mocks into RideServiceImp
    private RideServiceImp rideService;

    private Long userId = 1L;
    private Long driverId = 101L;
    private Long rideId = 1L;
    private UserDto userDto; // This instance will primarily be used to get non-ID related fields
    private DriverDto driverDto;
    private AvailableDriverDto availableDriverDto;
    private RideBookingRequestDto bookingRequestDto;
    private Rides rideEntity;
    private RideDto rideDto; // This instance will primarily be used for general setup and then specific mocks will return full DTOs

    @BeforeEach
    void setUp() {

        userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");

        driverDto = new DriverDto();
        driverDto.setId(driverId);
        driverDto.setFirstName("Jane");
        driverDto.setLastName("Smith");
        driverDto.setEmail("jane.smith@example.com");

        availableDriverDto = new AvailableDriverDto();
        availableDriverDto.setId(driverId);
        availableDriverDto.setFirstName("Jane");
        availableDriverDto.setLastName("Smith");
        availableDriverDto.setStatus(AvailableDriverDto.DriverStatus.AVAILABLE);

        bookingRequestDto = new RideBookingRequestDto();
        bookingRequestDto.setPickupLocation("A");
        bookingRequestDto.setDropoffLocation("B");
        bookingRequestDto.setActualFare(20.0f);
        bookingRequestDto.setDistance(10.0f);
        bookingRequestDto.setDuration("15min");

        rideEntity = new Rides();
        rideEntity.setId(rideId);
        rideEntity.setUserId(userId);
        rideEntity.setDriverId(driverId);
        rideEntity.setPickupLocation("A");
        rideEntity.setDropoffLocation("B");
        rideEntity.setActualFare(20.0f);
        rideEntity.setDistance(10.0f);
        rideEntity.setDuration("15min");
        rideEntity.setStatus(Rides.RideStatus.DRIVER_ASSIGNED);
        rideEntity.setStartTime(LocalDateTime.now());

        rideDto = new RideDto();
        rideDto.setId(rideId);
        rideDto.setUserId(userId);
        rideDto.setDriverId(driverId);
        rideDto.setPickupLocation("A");
        rideDto.setDropoffLocation("B");
        rideDto.setActualFare(20.0f);
        rideDto.setDistance(10.0f);
        rideDto.setStatus(Rides.RideStatus.DRIVER_ASSIGNED);
        rideDto.setStartTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("should return NOT_FOUND if user is not found")
    void requestRide_UserNotFound() {
        // Arrange
        ApiResponseDto<UserDto> userApiResponse = new ApiResponseDto<>("User Not Found", HttpStatus.NOT_FOUND, LocalDateTime.now(), null);
        when(userClient.getUserById(userId)).thenReturn(userApiResponse);

        // Act
        ApiResponseDto<RideDto> result = rideService.requestRide(userId, bookingRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getMessage()).contains("User not found");
        assertThat(result.getData()).isNull();

        // Verify no further interactions after user not found
        verify(userClient, times(1)).getUserById(userId);
        verifyNoInteractions(driverClient);
        verifyNoInteractions(rideRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("should return SERVICE_UNAVAILABLE if no drivers are available")
    void requestRide_NoDriversAvailable() {
        // Arrange
        UserDto userDtoFromClient = new UserDto();
        userDtoFromClient.setFirstName(userDto.getFirstName());
        userDtoFromClient.setLastName(userDto.getLastName());
        userDtoFromClient.setEmail(userDto.getEmail());

        ApiResponseDto<UserDto> userApiResponse = new ApiResponseDto<>("User Found", HttpStatus.OK, LocalDateTime.now(), userDtoFromClient);
        ApiResponseDto<List<AvailableDriverDto>> driversApiResponse = new ApiResponseDto<>("No Drivers", HttpStatus.OK, LocalDateTime.now(), Collections.emptyList());

        when(userClient.getUserById(userId)).thenReturn(userApiResponse);
        when(driverClient.getAllAvailableDriver()).thenReturn(driversApiResponse);
        when(rideRepository.save(any(Rides.class))).thenReturn(rideEntity); // Mock save for initial ride (SEARCHING_DRIVER)

        // Act
        ApiResponseDto<RideDto> result = rideService.requestRide(userId, bookingRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(result.getMessage()).contains("No Driver Available for ride");
        assertThat(result.getData()).isNull();

        // Verify interactions
        verify(userClient, times(1)).getUserById(userId);
        verify(driverClient, times(1)).getAllAvailableDriver();
        verify(rideRepository, times(1)).save(any(Rides.class)); // Only the initial save (SEARCHING_DRIVER)
        verifyNoMoreInteractions(driverClient); // No driver status update
        verifyNoInteractions(modelMapper);
    }

    // --- Tests for getUsersRides method ---

    @Test
    @DisplayName("should return a list of rides for a given user ID")
    void getUsersRides_Success() {
        // Arrange
        List<Rides> userRidesEntities = Arrays.asList(rideEntity); // Assuming rideEntity is for userId 1
        when(rideRepository.findByUserId(userId)).thenReturn(userRidesEntities);

        UserDto fetchedUserDto = new UserDto();
        fetchedUserDto.setFirstName(userDto.getFirstName());
        fetchedUserDto.setLastName(userDto.getLastName());
        fetchedUserDto.setEmail(userDto.getEmail());

        ApiResponseDto<UserDto> userApiResponse = new ApiResponseDto<>("User Found", HttpStatus.OK, LocalDateTime.now(), fetchedUserDto);
        ApiResponseDto<DriverDto> driverApiResponse = new ApiResponseDto<>("Driver Found", HttpStatus.OK, LocalDateTime.now(), driverDto);

        when(userClient.getUserById(userId)).thenReturn(userApiResponse);
        when(driverClient.findDriverById(driverId)).thenReturn(driverApiResponse);

        RideDto mappedRideDto = new RideDto();
        mappedRideDto.setId(rideId);
        mappedRideDto.setUserId(userId);
        mappedRideDto.setDriverId(driverId);
        mappedRideDto.setPickupLocation(rideEntity.getPickupLocation());
        mappedRideDto.setDropoffLocation(rideEntity.getDropoffLocation());
        mappedRideDto.setActualFare(rideEntity.getActualFare());
        mappedRideDto.setDistance(rideEntity.getDistance());
        mappedRideDto.setStatus(rideEntity.getStatus());
        mappedRideDto.setStartTime(rideEntity.getStartTime());
        mappedRideDto.setUserFirstName(fetchedUserDto.getFirstName());
        mappedRideDto.setUserlastName(fetchedUserDto.getLastName());
        mappedRideDto.setDriverFullname(driverDto.getFirstName(), driverDto.getLastName());

        when(modelMapper.map(any(Rides.class), eq(RideDto.class))).thenReturn(mappedRideDto);

        // Act
        List<RideDto> result = rideService.getUsersRides(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(rideId);
        assertThat(result.get(0).getUserFirstName()).isEqualTo(fetchedUserDto.getFirstName());
        assertThat(result.get(0).getDriverFullname()).contains(driverDto.getFirstName());

        // Verify interactions
        verify(rideRepository, times(1)).findByUserId(userId);
        verify(userClient, times(1)).getUserById(userId);
        verify(driverClient, times(1)).findDriverById(driverId);
        verify(modelMapper, times(1)).map(any(Rides.class), eq(RideDto.class));
    }

    @Test
    @DisplayName("should throw RideNotFoundException if no rides found for user ID")
    void getUsersRides_NotFound() {
        // Arrange
        when(rideRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());

        // Act & Assert
        RideNotFoundException thrown = assertThrows(RideNotFoundException.class, () -> {
            rideService.getUsersRides(userId);
        });

        assertThat(thrown.getMessage()).contains("No rides found for user ID: " + userId);
        verify(rideRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(userClient);
        verifyNoInteractions(driverClient);
        verifyNoInteractions(modelMapper);
    }

    // --- Tests for getDriverRidesDetails method ---

    @Test
    @DisplayName("should return a list of rides for a given driver ID")
    void getDriverRidesDetails_Success() {
        // Arrange
        List<Rides> driverRidesEntities = Arrays.asList(rideEntity);
        when(rideRepository.findByDriverId(driverId)).thenReturn(driverRidesEntities);

        UserDto fetchedUserDto = new UserDto();
        fetchedUserDto.setFirstName(userDto.getFirstName());
        fetchedUserDto.setLastName(userDto.getLastName());
        fetchedUserDto.setEmail(userDto.getEmail());
        ApiResponseDto<UserDto> userApiResponse = new ApiResponseDto<>("User Found", HttpStatus.OK, LocalDateTime.now(), fetchedUserDto);
        ApiResponseDto<DriverDto> driverApiResponse = new ApiResponseDto<>("Driver Found", HttpStatus.OK, LocalDateTime.now(), driverDto);

        when(userClient.getUserById(userId)).thenReturn(userApiResponse);
        when(driverClient.findDriverById(driverId)).thenReturn(driverApiResponse);

        RideDto mappedRideDto = new RideDto();
        mappedRideDto.setId(rideId);
        mappedRideDto.setUserId(userId);
        mappedRideDto.setDriverId(driverId);
        mappedRideDto.setPickupLocation(rideEntity.getPickupLocation());
        mappedRideDto.setDropoffLocation(rideEntity.getDropoffLocation());
        mappedRideDto.setActualFare(rideEntity.getActualFare());
        mappedRideDto.setDistance(rideEntity.getDistance());
        mappedRideDto.setStatus(rideEntity.getStatus());
        mappedRideDto.setStartTime(rideEntity.getStartTime());
        mappedRideDto.setUserFirstName(fetchedUserDto.getFirstName());
        mappedRideDto.setUserlastName(fetchedUserDto.getLastName());
        mappedRideDto.setDriverFullname(driverDto.getFirstName(), driverDto.getLastName());

        when(modelMapper.map(any(Rides.class), eq(RideDto.class))).thenReturn(mappedRideDto);

        // Act
        List<RideDto> result = rideService.getDriverRidesDetails(driverId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(rideId);
        assertThat(result.get(0).getDriverFullname()).contains(driverDto.getFirstName());
        assertThat(result.get(0).getUserFirstName()).isEqualTo(fetchedUserDto.getFirstName());

        // Verify interactions
        verify(rideRepository, times(1)).findByDriverId(driverId);
        verify(userClient, times(1)).getUserById(userId);
        verify(driverClient, times(1)).findDriverById(driverId);
        verify(modelMapper, times(1)).map(any(Rides.class), eq(RideDto.class));
    }

    @Test
    @DisplayName("should throw RideNotFoundException if no rides found for driver ID")
    void getDriverRidesDetails_NotFound() {
        // Arrange
        when(rideRepository.findByDriverId(anyLong())).thenReturn(Collections.emptyList());

        // Act & Assert
        RideNotFoundException thrown = assertThrows(RideNotFoundException.class, () -> {
            rideService.getDriverRidesDetails(driverId);
        });

        assertThat(thrown.getMessage()).contains("No rides found for driver ID: " + driverId);
        verify(rideRepository, times(1)).findByDriverId(driverId);
        verifyNoInteractions(userClient);
        verifyNoInteractions(driverClient);
        verifyNoInteractions(modelMapper);
    }

    // --- Tests for updateRideStatus method ---

    @Test
    @DisplayName("should update ride status successfully")
    void updateRideStatus_Success() {
        // Arrange
        Rides.RideStatus newStatus = Rides.RideStatus.RIDE_STARTED;
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(rideEntity));
        when(rideRepository.save(any(Rides.class))).thenReturn(rideEntity);

        // Act
        ApiResponseDto<String> result = rideService.updateRideStatus(rideId, newStatus);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getMessage()).contains("Ride Status Changed Successfully");
        verify(rideRepository, times(1)).findById(rideId);
        verify(rideRepository, times(1)).save(rideEntity);
        assertThat(rideEntity.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("should set endTime when status is COMPLETED")
    void updateRideStatus_CompletedSetsEndTime() {
        // Arrange
        Rides.RideStatus newStatus = Rides.RideStatus.COMPLETED;
        when(rideRepository.findById(rideId)).thenReturn(Optional.of(rideEntity));
        when(rideRepository.save(any(Rides.class))).thenReturn(rideEntity);

        // Act
        rideService.updateRideStatus(rideId, newStatus);

        // Assert
        assertThat(rideEntity.getStatus()).isEqualTo(newStatus);
        assertThat(rideEntity.getEndTime()).isNotNull();
        assertThat(rideEntity.getEndTime()).isBetween(LocalDateTime.now().minusSeconds(5), LocalDateTime.now().plusSeconds(5));
        verify(rideRepository, times(1)).save(rideEntity);
    }

    @Test
    @DisplayName("should throw RideNotFoundException when updating status for non-existent ride")
    void updateRideStatus_NotFound() {
        // Arrange
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RideNotFoundException thrown = assertThrows(RideNotFoundException.class, () -> {
            rideService.updateRideStatus(rideId, Rides.RideStatus.RIDE_STARTED);
        });

        assertThat(thrown.getMessage()).contains("Ride Not found with ID: " + rideId);
        verify(rideRepository, times(1)).findById(rideId);
        verify(rideRepository, never()).save(any(Rides.class));
    }

    // --- Tests for cancelRide method ---

    @Test
    @DisplayName("should successfully cancel a ride by user and update driver status")
    void cancelRide_ByUser_Success() {
        // Arrange
        RideStatusUpdateRequest updateRequest = new RideStatusUpdateRequest();
        updateRequest.setStatus(Rides.RideStatus.CANCELLED_BY_USER.name());

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(rideEntity));
        when(rideRepository.save(any(Rides.class))).thenReturn(rideEntity);
        doNothing().when(driverClient).updateDriverStatus(eq(driverId), eq(AvailableDriverDto.DriverStatus.AVAILABLE));

        // Act
        ApiResponseDto<String> result = rideService.cancelRide(rideId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getMessage()).contains("Ride has been cancelled");
        assertThat(rideEntity.getStatus()).isEqualTo(Rides.RideStatus.CANCELLED_BY_USER);

        verify(rideRepository, times(1)).findById(rideId);
        verify(rideRepository, times(1)).save(rideEntity);
        verify(driverClient, times(1)).updateDriverStatus(driverId, AvailableDriverDto.DriverStatus.AVAILABLE);
    }

    @Test
    @DisplayName("should successfully cancel a ride by driver and update driver status")
    void cancelRide_ByDriver_Success() {
        // Arrange
        RideStatusUpdateRequest updateRequest = new RideStatusUpdateRequest();
        updateRequest.setStatus(Rides.RideStatus.CANCELLED_BY_DRIVER.name());

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(rideEntity));
        when(rideRepository.save(any(Rides.class))).thenReturn(rideEntity);
        doNothing().when(driverClient).updateDriverStatus(eq(driverId), eq(AvailableDriverDto.DriverStatus.AVAILABLE));

        // Act
        ApiResponseDto<String> result = rideService.cancelRide(rideId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getMessage()).contains("Ride has been cancelled");
        assertThat(rideEntity.getStatus()).isEqualTo(Rides.RideStatus.CANCELLED_BY_DRIVER);

        verify(rideRepository, times(1)).findById(rideId);
        verify(rideRepository, times(1)).save(rideEntity);
        verify(driverClient, times(1)).updateDriverStatus(driverId, AvailableDriverDto.DriverStatus.AVAILABLE);
    }

    @Test
    @DisplayName("should throw RideNotFoundException when cancelling non-existent ride")
    void cancelRide_NotFound() {
        // Arrange
        RideStatusUpdateRequest updateRequest = new RideStatusUpdateRequest();
        updateRequest.setStatus(Rides.RideStatus.CANCELLED_BY_USER.name());
        when(rideRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RideNotFoundException thrown = assertThrows(RideNotFoundException.class, () -> {
            rideService.cancelRide(rideId, updateRequest);
        });

        assertThat(thrown.getMessage()).contains("Ride not found with ID: " + rideId);
        verify(rideRepository, times(1)).findById(rideId);
        verify(rideRepository, never()).save(any(Rides.class));
        verifyNoInteractions(driverClient);
    }

    @Test
    @DisplayName("should handle driver client update failure during cancellation gracefully")
    void cancelRide_DriverClientUpdateFails() {
        // Arrange
        RideStatusUpdateRequest updateRequest = new RideStatusUpdateRequest();
        updateRequest.setStatus(Rides.RideStatus.CANCELLED_BY_USER.name());

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(rideEntity));
        when(rideRepository.save(any(Rides.class))).thenReturn(rideEntity);
        doThrow(new RuntimeException("Feign error")).when(driverClient).updateDriverStatus(anyLong(), any(AvailableDriverDto.DriverStatus.class));

        // Act
        ApiResponseDto<String> result = rideService.cancelRide(rideId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getMessage()).contains("Ride has been cancelled");
        assertThat(rideEntity.getStatus()).isEqualTo(Rides.RideStatus.CANCELLED_BY_USER);

        verify(rideRepository, times(1)).findById(rideId);
        verify(rideRepository, times(1)).save(rideEntity);
        verify(driverClient, times(1)).updateDriverStatus(driverId, AvailableDriverDto.DriverStatus.AVAILABLE);
    }

    // --- Tests for getLatestAssignedRideForDriver method ---

    @Test
    @DisplayName("should return the latest assigned ride for a driver")
    void getLatestAssignedRideForDriver_Success() {
        // Arrange
        when(rideRepository.findFirstByDriverIdAndStatusOrderByStartTimeDesc(driverId, Rides.RideStatus.DRIVER_ASSIGNED))
                .thenReturn(Optional.of(rideEntity));

        // Act
        Rides result = rideService.getLatestAssignedRideForDriver(driverId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(rideId);
        assertThat(result.getDriverId()).isEqualTo(driverId);
        assertThat(result.getStatus()).isEqualTo(Rides.RideStatus.DRIVER_ASSIGNED);

        verify(rideRepository, times(1)).findFirstByDriverIdAndStatusOrderByStartTimeDesc(driverId, Rides.RideStatus.DRIVER_ASSIGNED);
    }

    @Test
    @DisplayName("should throw RideNotFoundException if no latest assigned ride found for driver")
    void getLatestAssignedRideForDriver_NotFound() {
        // Arrange
        when(rideRepository.findFirstByDriverIdAndStatusOrderByStartTimeDesc(anyLong(), eq(Rides.RideStatus.DRIVER_ASSIGNED)))
                .thenReturn(Optional.empty());

        // Act & Assert
        RideNotFoundException thrown = assertThrows(RideNotFoundException.class, () -> {
            rideService.getLatestAssignedRideForDriver(driverId);
        });

        assertThat(thrown.getMessage()).contains("No DRIVER_ASSIGNED ride found for driver ID: " + driverId);
        verify(rideRepository, times(1)).findFirstByDriverIdAndStatusOrderByStartTimeDesc(driverId, Rides.RideStatus.DRIVER_ASSIGNED);
    }


    // --- Tests for enrichRideDetails method (private, but implicitly tested) ---

    @Test
    @DisplayName("enrichRideDetails should handle null user response from UserClient gracefully")
    void enrichRideDetails_NullUserResponse() {
        // Arrange
        RideDto mappedRideDto = new RideDto();
        mappedRideDto.setId(rideId);
        mappedRideDto.setUserId(userId);
        mappedRideDto.setDriverId(driverId);
        mappedRideDto.setPickupLocation(rideEntity.getPickupLocation());
        mappedRideDto.setDropoffLocation(rideEntity.getDropoffLocation());
        mappedRideDto.setActualFare(rideEntity.getActualFare());
        mappedRideDto.setDistance(rideEntity.getDistance());
        mappedRideDto.setStatus(rideEntity.getStatus());
        mappedRideDto.setStartTime(rideEntity.getStartTime());
        mappedRideDto.setDriverFullname(driverDto.getFirstName(), driverDto.getLastName());

        when(modelMapper.map(any(Rides.class), eq(RideDto.class))).thenReturn(mappedRideDto);
        when(userClient.getUserById(userId)).thenReturn(null);
        ApiResponseDto<DriverDto> driverApiResponse = new ApiResponseDto<>("Driver Found", HttpStatus.OK, LocalDateTime.now(), driverDto);
        when(driverClient.findDriverById(driverId)).thenReturn(driverApiResponse);

        // Act
        RideDto result = rideService.enrichRideDetails(rideEntity);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserFirstName()).isNull();
        assertThat(result.getUserlastName()).isNull();
        assertThat(result.getDriverFullname()).contains(driverDto.getFirstName());

        verify(userClient, times(1)).getUserById(userId);
        verify(driverClient, times(1)).findDriverById(driverId);
        verify(modelMapper, times(1)).map(any(Rides.class), eq(RideDto.class));
    }

    @Test
    @DisplayName("enrichRideDetails should handle empty user data in response from UserClient gracefully")
    void enrichRideDetails_EmptyUserDataInResponse() {
        // Arrange
        RideDto mappedRideDto = new RideDto();
        mappedRideDto.setId(rideId);
        mappedRideDto.setUserId(userId);
        mappedRideDto.setDriverId(driverId);
        mappedRideDto.setPickupLocation(rideEntity.getPickupLocation());
        mappedRideDto.setDropoffLocation(rideEntity.getDropoffLocation());
        mappedRideDto.setActualFare(rideEntity.getActualFare());
        mappedRideDto.setDistance(rideEntity.getDistance());
        mappedRideDto.setStatus(rideEntity.getStatus());
        mappedRideDto.setStartTime(rideEntity.getStartTime());
        mappedRideDto.setDriverFullname(driverDto.getFirstName(), driverDto.getLastName());

        when(modelMapper.map(any(Rides.class), eq(RideDto.class))).thenReturn(mappedRideDto);
        ApiResponseDto<UserDto> userApiResponse = new ApiResponseDto<>("User Not Found", HttpStatus.NOT_FOUND, LocalDateTime.now(), null);
        when(userClient.getUserById(userId)).thenReturn(userApiResponse);
        ApiResponseDto<DriverDto> driverApiResponse = new ApiResponseDto<>("Driver Found", HttpStatus.OK, LocalDateTime.now(), driverDto);
        when(driverClient.findDriverById(driverId)).thenReturn(driverApiResponse);

        // Act
        RideDto result = rideService.enrichRideDetails(rideEntity);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserFirstName()).isNull();
        assertThat(result.getUserlastName()).isNull();
        assertThat(result.getDriverFullname()).contains(driverDto.getFirstName());

        verify(userClient, times(1)).getUserById(userId);
        verify(driverClient, times(1)).findDriverById(driverId);
        verify(modelMapper, times(1)).map(any(Rides.class), eq(RideDto.class));
    }

    @Test
    @DisplayName("enrichRideDetails should handle user client exception gracefully")
    void enrichRideDetails_UserClientException() {
        // Arrange
        RideDto mappedRideDto = new RideDto();
        mappedRideDto.setId(rideId);
        mappedRideDto.setUserId(userId);
        mappedRideDto.setDriverId(driverId);
        mappedRideDto.setPickupLocation(rideEntity.getPickupLocation());
        mappedRideDto.setDropoffLocation(rideEntity.getDropoffLocation());
        mappedRideDto.setActualFare(rideEntity.getActualFare());
        mappedRideDto.setDistance(rideEntity.getDistance());
        mappedRideDto.setStatus(rideEntity.getStatus());
        mappedRideDto.setStartTime(rideEntity.getStartTime());
        mappedRideDto.setDriverFullname(driverDto.getFirstName(), driverDto.getLastName());

        when(modelMapper.map(any(Rides.class), eq(RideDto.class))).thenReturn(mappedRideDto);
        when(userClient.getUserById(userId)).thenThrow(new RuntimeException("User client unavailable"));
        ApiResponseDto<DriverDto> driverApiResponse = new ApiResponseDto<>("Driver Found", HttpStatus.OK, LocalDateTime.now(), driverDto);
        when(driverClient.findDriverById(driverId)).thenReturn(driverApiResponse);

        // Act
        RideDto result = rideService.enrichRideDetails(rideEntity);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserFirstName()).isNull();
        assertThat(result.getDriverFullname()).contains(driverDto.getFirstName());

        verify(userClient, times(1)).getUserById(userId);
        verify(driverClient, times(1)).findDriverById(driverId);
        verify(modelMapper, times(1)).map(any(Rides.class), eq(RideDto.class));
    }

    @Test
    @DisplayName("enrichRideDetails should handle driver client exception gracefully")
    void enrichRideDetails_DriverClientException() {
        // Arrange
        RideDto mappedRideDto = new RideDto();
        mappedRideDto.setId(rideId);
        mappedRideDto.setUserId(userId);
        mappedRideDto.setDriverId(driverId);
        mappedRideDto.setPickupLocation(rideEntity.getPickupLocation());
        mappedRideDto.setDropoffLocation(rideEntity.getDropoffLocation());
        mappedRideDto.setActualFare(rideEntity.getActualFare());
        mappedRideDto.setDistance(rideEntity.getDistance());
        mappedRideDto.setStatus(rideEntity.getStatus());
        mappedRideDto.setStartTime(rideEntity.getStartTime());
        mappedRideDto.setUserFirstName(userDto.getFirstName());
        mappedRideDto.setUserlastName(userDto.getLastName());

        when(modelMapper.map(any(Rides.class), eq(RideDto.class))).thenReturn(mappedRideDto);
        UserDto fetchedUserDto = new UserDto();
        fetchedUserDto.setFirstName(userDto.getFirstName());
        fetchedUserDto.setLastName(userDto.getLastName());
        fetchedUserDto.setEmail(userDto.getEmail());
        ApiResponseDto<UserDto> userApiResponse = new ApiResponseDto<>("User Found", HttpStatus.OK, LocalDateTime.now(), fetchedUserDto);
        when(userClient.getUserById(userId)).thenReturn(userApiResponse);
        when(driverClient.findDriverById(driverId)).thenThrow(new RuntimeException("Driver client unavailable"));

        // Act
        RideDto result = rideService.enrichRideDetails(rideEntity);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(result.getDriverFullname()).isNull();

        verify(userClient, times(1)).getUserById(userId);
        verify(driverClient, times(1)).findDriverById(driverId);
        verify(modelMapper, times(1)).map(any(Rides.class), eq(RideDto.class));
    }
}