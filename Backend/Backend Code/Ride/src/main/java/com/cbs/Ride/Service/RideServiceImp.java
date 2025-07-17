package com.cbs.Ride.Service;

import com.cbs.Ride.Client.DriverClient;
import com.cbs.Ride.Client.UserClient;
import com.cbs.Ride.Dto.*;
import com.cbs.Ride.Entity.Rides;
import com.cbs.Ride.Exception.RideNotFoundException;
import com.cbs.Ride.Repository.RideRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RideServiceImp implements IRideService {
    private static final Logger logger = LoggerFactory.getLogger(RideServiceImp.class);

    private final RideRepository rideRepository;
    private final UserClient userClient;
    private final DriverClient driverClient;
    private final ModelMapper modelMapper;

    @Autowired
    public RideServiceImp(RideRepository rideRepository, UserClient userClient, DriverClient driverClient, ModelMapper modelMapper) {
        this.rideRepository = rideRepository;
        this.userClient = userClient;
        this.driverClient = driverClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public ApiResponseDto<RideDto> requestRide(long userID, RideBookingRequestDto requestDto) {
        logger.info("Received ride request for user ID: {} from pickup {} to dropoff {}", userID, requestDto.getPickupLocation(), requestDto.getDropoffLocation());
        logger.debug("RideBookingRequestDto: {}", requestDto);

        // --- 1. Fetch User Details ---
        ApiResponseDto<UserDto> userResponse = userClient.getUserById(userID);
        if (userResponse == null || userResponse.getData() == null) {
            logger.warn("User not found or empty response from User Service for ID: {}", userID);
            return new ApiResponseDto<>("User not found or empty response from User Service", HttpStatus.NOT_FOUND, LocalDateTime.now(), null);
        }
        UserDto userData = userResponse.getData();
        logger.debug("User details fetched: {}", userData);

        Rides ride = new Rides();
        ride.setUserId(userID);
        ride.setPickupLocation(requestDto.getPickupLocation());
        ride.setDropoffLocation(requestDto.getDropoffLocation());
        ride.setActualFare(requestDto.getActualFare());
        ride.setDistance(requestDto.getDistance());
        ride.setDuration(requestDto.getDuration());
        ride.setStatus(Rides.RideStatus.SEARCHING_DRIVER); // Set initial status here
        logger.debug("Initial ride entity created: {}", ride);

        //  Find and Assign Driver
        ApiResponseDto<List<AvailableDriverDto>> availableDriversResponse = driverClient.getAllAvailableDriver();

        if (availableDriversResponse.getData() == null || availableDriversResponse.getData().isEmpty()) {
            logger.warn("No drivers available for user ID: {}. Ride saved with status: {}", userID, ride.getStatus());
            rideRepository.save(ride); // Save the ride even if no driver is found to record the request
            return new ApiResponseDto<>("No Driver Available for ride. Ride saved with status: " + ride.getStatus(), HttpStatus.SERVICE_UNAVAILABLE, LocalDateTime.now(), null);
        } else {
            AvailableDriverDto assignedDriver = availableDriversResponse.getData().getFirst();
            logger.info("Assigned Driver ID: {} for ride request by user ID: {}", assignedDriver.getId(), userID);
            logger.debug("Assigned Driver details: {}", assignedDriver);

            ride.setStartTime(LocalDateTime.now());
            ride.setDriverId(assignedDriver.getId());
            ride.setStatus(Rides.RideStatus.DRIVER_ASSIGNED);

            driverClient.updateDriverStatus(assignedDriver.getId(), AvailableDriverDto.DriverStatus.ON_RIDE);
            logger.info("Driver ID: {} status updated to ON_RIDE.", assignedDriver.getId());

            rideRepository.save(ride); // Save the ride with assigned driver details
            logger.info("Ride ID: {} saved with driver ID: {} assigned.", ride.getId(), assignedDriver.getId());

            RideDto rideDetailsDto = modelMapper.map(ride, RideDto.class);
            rideDetailsDto.setDriverFullname(assignedDriver.getFirstName(), assignedDriver.getLastName());
            rideDetailsDto.setUserFirstName(userData.getFirstName());
            rideDetailsDto.setUserlastName(userData.getLastName());


            String successMessage = String.format("Ride booked! Driver %s %s has been assigned.",
                    assignedDriver.getFirstName(), assignedDriver.getLastName());
            logger.info(successMessage);
            return new ApiResponseDto<>(successMessage, HttpStatus.CREATED, LocalDateTime.now(), rideDetailsDto);
        }
    }

    @Override
    public List<RideDto> getUsersRides(long userId) {
        logger.info("Fetching rides for user ID: {}", userId);
        List<Rides> ridesDetails = rideRepository.findByUserId(userId);
        if (ridesDetails.isEmpty()) {
            logger.warn("No rides found for user ID: {}", userId);
            throw new RideNotFoundException("No rides found for user ID: " + userId);
        }
        List<RideDto> rideDtos = ridesDetails.stream()
                .map(this::enrichRideDetails)
                .collect(Collectors.toList());
        logger.info("Found {} rides for user ID: {}", rideDtos.size(), userId);
        return rideDtos;
    }

    public RideDto enrichRideDetails(Rides ride) {
        logger.debug("Enriching details for ride ID: {}", ride.getId());

        // ⭐ IMPORTANT DEBUG LINE ⭐
        logger.debug("BEFORE MAPPING: Ride ID: {}, Rating from Rides entity: {}", ride.getId(), ride.getRating());
        System.out.println("inside enrichDeatils Functions ");
        RideDto rideDetails = modelMapper.map(ride, RideDto.class);
        // ⭐ IMPORTANT DEBUG LINE ⭐
        logger.debug("AFTER MAPPING (initial): Ride ID: {}, Rating in RideDto: {}", ride.getId(), rideDetails.getRating());

        if (ride.getUserId() != null) {
            try {
                ApiResponseDto<UserDto> user = userClient.getUserById(ride.getUserId());
                if (user != null && user.getData() != null) {
                    rideDetails.setUserFirstName(user.getData().getFirstName());
                    rideDetails.setUserlastName(user.getData().getLastName());
                } else {
                    logger.warn("Could not fetch user details for user ID: {} for ride ID: {}", ride.getUserId(), ride.getId());
                }
            } catch (Exception e) {
                logger.error("Error fetching user details for user ID: {} for ride ID: {}: {}", ride.getUserId(), ride.getId(), e.getMessage());
            }
        }

        if (ride.getDriverId() != null) {
            try {
                ApiResponseDto<DriverDto> driver = driverClient.findDriverById(ride.getDriverId());
                if (driver != null && driver.getData() != null) {
                    rideDetails.setDriverFullname(driver.getData().getFirstName(), driver.getData().getLastName());
                } else {
                    logger.warn("Could not fetch driver details for driver ID: {} for ride ID: {}", ride.getDriverId(), ride.getId());
                }
            } catch (Exception e) {
                logger.error("Error fetching driver details for driver ID: {} for ride ID: {}: {}", ride.getDriverId(), ride.getId(), e.getMessage());
            }
        }
        logger.debug("Enriched ride details for ride ID {}: {}", ride.getId(), rideDetails);
        return rideDetails;
    }

    @Override
    public ApiResponseDto<String> updateRideStatus(Long id, Rides.RideStatus status) {
        logger.info("Updating ride ID: {} status to: {}", id, status);
        Optional<Rides> rideOptional = rideRepository.findById(id);
        if (rideOptional.isEmpty()) {
            logger.warn("Ride with ID {} not found for status update.", id);
            throw new RideNotFoundException("Ride Not found with ID: " + id);
        }

        Rides rideDetails = rideOptional.get();
        rideDetails.setStatus(status);
        if (status == Rides.RideStatus.COMPLETED) {
            rideDetails.setEndTime(LocalDateTime.now());
            logger.info("Ride {} completed. End time set to: {}", id, rideDetails.getEndTime());
        }
        rideRepository.save(rideDetails);
        logger.info("Ride ID: {} status successfully changed to {}.", id, status);
        return new ApiResponseDto<>("Ride Status Changed Successfully", HttpStatus.OK, LocalDateTime.now(), null);
    }

    @Override
    public List<RideDto> getDriverRidesDetails(long driverId) {
        logger.info("Fetching rides for driver ID: {}", driverId);
        List<Rides> ridesDetails = rideRepository.findByDriverId(driverId);
        if (ridesDetails.isEmpty()) {
            logger.warn("No rides found for driver ID: {}", driverId);
            throw new RideNotFoundException("No rides found for driver ID: " + driverId);
        }
        List<RideDto> rideDtos = ridesDetails.stream()
                .map(this::enrichRideDetails)
                .collect(Collectors.toList());
        logger.info("Found {} rides for driver ID: {}", rideDtos.size(), driverId);
        System.out.println("Inside Driver Function ");
        return rideDtos;
    }

    @Override
    public ApiResponseDto<String> cancelRide(long id, RideStatusUpdateRequest updateRequest) {
        logger.info("Attempting to cancel ride ID: {}", id);
        Rides.RideStatus status = Rides.RideStatus.valueOf(updateRequest.getStatus());
        Optional<Rides> ridesOptional = rideRepository.findById(id);

        if (ridesOptional.isEmpty()) {
            logger.warn("Ride with ID {} not found for cancellation.", id);
            throw new RideNotFoundException("Ride not found with ID: " + id);
        }

        Rides rides = ridesOptional.get();
        rides.setStatus(status); // Set status to CANCELLED
        rideRepository.save(rides);
        logger.info("Ride ID: {} status updated to {}.", id, status);

        // If a driver was assigned, make them available again
        if (rides.getDriverId() != null) {
            try {
                driverClient.updateDriverStatus(rides.getDriverId(), AvailableDriverDto.DriverStatus.AVAILABLE);
                logger.info("Driver ID: {} status updated to AVAILABLE after ride cancellation.", rides.getDriverId());
            } catch (Exception e) {
                logger.error("Failed to update driver status to AVAILABLE for driver ID {} after ride cancellation: {}", rides.getDriverId(), e.getMessage(), e);
            }
        }
        return new ApiResponseDto<>("Ride has been cancelled ", HttpStatus.OK, LocalDateTime.now(), null);
    }
    @Override
    public Void addRating(Long rideId, int rating) {
        Optional<Rides> optionalDriver = rideRepository.findById(rideId);
        if(optionalDriver.isPresent()){
            optionalDriver.get().setRating(rating);
            rideRepository.save(optionalDriver.get());
        }
        logger.info("Rating Saved into the database");

        return null;
    }

    @Override
    public Rides getLatestAssignedRideForUser(Long userId) {
        logger.info("Fetching latest assigned ride for User ID: {}", userId);
        Optional<Rides> latestRide = rideRepository.findFirstByUserIdAndStatusOrderByStartTimeDesc(userId, Rides.RideStatus.COMPLETED);

        if (latestRide.isPresent()) {
            logger.info("Found latest assigned ride ID: {} for User ID: {}", latestRide.get().getId(), userId);
            return latestRide.get();
        } else {
            logger.warn("No User  ride found for User ID: {}", userId);
            throw new RideNotFoundException("No User ride found for driver ID: " + userId);
        }
//        return latestRide.get();
    }

    @Override
    public Rides getLatestAssignedRideForDriver(Long driverId) {
        logger.info("Fetching latest assigned ride for driver ID: {}", driverId);
        // Assuming findFirstByDriverIdAndStatusOrderByStartTimeDesc works correctly.
        // It fetches the latest ride where the driver was assigned and the status is DRIVER_ASSIGNED.
        Optional<Rides> latestRide = rideRepository.findFirstByDriverIdAndStatusOrderByStartTimeDesc(driverId, Rides.RideStatus.DRIVER_ASSIGNED);

        if (latestRide.isPresent()) {
            logger.info("Found latest assigned ride ID: {} for driver ID: {}", latestRide.get().getId(), driverId);
            return latestRide.get();
        } else {
            logger.warn("No DRIVER_ASSIGNED ride found for driver ID: {}", driverId);
            throw new RideNotFoundException("No DRIVER_ASSIGNED ride found for driver ID: " + driverId);
        }
    }
}