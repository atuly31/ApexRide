package com.cbs.Driver.Service;

import com.cbs.Driver.Client.AdminServiceFeignClient;
import com.cbs.Driver.Client.PaymentInitiateClient;
import com.cbs.Driver.Client.RideClient;
import com.cbs.Driver.Entity.Driver;
import com.cbs.Driver.Exception.AlreadyExistException;
import com.cbs.Driver.Exception.DriverDoesNotExistException;
import com.cbs.Driver.Exception.DriverNotAvailableException;
import com.cbs.Driver.Exception.DriverServiceCommunicationException;
import com.cbs.Driver.Repository.IDriverRepository;
import com.cbs.Driver.dto.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements IDriverService {
    private static final Logger logger = LoggerFactory.getLogger(DriverServiceImpl.class);

    private final IDriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private final RideClient rideClient;
    private final AdminServiceFeignClient adminClient;
    private final PaymentInitiateClient paymentInitiateClient;

    // Constructor Injection for Autowiring
    @Autowired
    public DriverServiceImpl(IDriverRepository driverRepository, ModelMapper modelMapper, RideClient rideClient,
                             AdminServiceFeignClient adminClient,
                             PaymentInitiateClient paymentInitiateClient) {
        this.driverRepository = driverRepository;
        this.modelMapper = modelMapper;
        this.rideClient = rideClient;
        this.adminClient = adminClient;
        this.paymentInitiateClient = paymentInitiateClient;
    }

    @Override
    public ApiResponseDto<DriverRegistrationDto> registerDriver(DriverRegistrationDto driverRegistrationDto) {
        logger.info("Attempting to register driver with license number: {}", driverRegistrationDto.getLicenseNumber());
        logger.debug("Driver registration DTO: {}", driverRegistrationDto);

        if (driverRepository.findByLicenseNumber(driverRegistrationDto.getLicenseNumber()).isPresent()) {
            logger.warn("Registration failed: License Number '{}' already registered.", driverRegistrationDto.getLicenseNumber());
            throw new AlreadyExistException("License Number Already Registered");
        }
        if (driverRepository.findByPhoneNumber(driverRegistrationDto.getPhoneNumber()).isPresent()) {
            logger.warn("Registration failed: Phone Number '{}' already registered.", driverRegistrationDto.getPhoneNumber());
            throw new AlreadyExistException("Phone Number Already Registered");
        }

        Driver mappedDriver = modelMapper.map(driverRegistrationDto, Driver.class);
        mappedDriver.setRegistrationDate(LocalDateTime.now());
        mappedDriver.setApproved(false);
        mappedDriver.setStatus(Driver.DriverStatus.OFFLINE);
        logger.debug("Mapped driver entity before saving: {}", mappedDriver);

        Driver savedDriver = driverRepository.save(mappedDriver);
        logger.info("Driver saved successfully with ID: {}", savedDriver.getId());

        try {
            DriverRegistrationRequestToAdminDTO adminRequest = new DriverRegistrationRequestToAdminDTO(
                    savedDriver.getFirstName() + " " + savedDriver.getLastName(),
                    savedDriver.getLicenseNumber(),
                    savedDriver.getVehicleModel(),
                    savedDriver.getPhoneNumber(),
                    savedDriver.getId()
            );
            System.out.println(savedDriver.getLicenseNumber());
            logger.info("Sending driver registration request to admin service for license number: {}", savedDriver.getLicenseNumber());
            adminClient.receiveDriverRegistrationForApproval(adminRequest);
            logger.info("Driver registration request successfully sent to admin service.");
        } catch (feign.FeignException fe) {
            logger.error("Feign Client Error calling Admin Service for driver with ID '{}': {}", savedDriver.getId(), fe.getMessage(), fe);
            try {
                driverRepository.delete(savedDriver);
                logger.info("Driver with ID {} deleted successfully due to Admin service error.", savedDriver.getId());
            } catch (Exception deleteEx) {
                logger.error("Failed to delete driver with ID {} after Admin service error: {}", savedDriver.getId(), deleteEx.getMessage(), deleteEx);
            }
            throw new DriverServiceCommunicationException("Failed to register driver in Admin Service: " + fe.getMessage(), fe, savedDriver.getId());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while sending driver registration to admin service for driver with ID '{}': {}", savedDriver.getId(), e.getMessage(), e);
            try {
                driverRepository.delete(savedDriver);
                logger.info("Driver with ID {} deleted successfully due to unexpected error during Admin service call.", savedDriver.getId());
            } catch (Exception deleteEx) {
                logger.error("Failed to delete driver with ID {} after unexpected error: {}", savedDriver.getId(), deleteEx.getMessage(), deleteEx);
            }
            throw new DriverServiceCommunicationException("An unexpected error occurred while communicating with Admin Service: " + e.getMessage(), e, savedDriver.getId());
        }

        DriverRegistrationDto savedDriverDto = modelMapper.map(savedDriver, DriverRegistrationDto.class);
        savedDriverDto.setPasswordHash(""); // Always clear sensitive info before returning

        return new ApiResponseDto<>("Registered Successfully", HttpStatus.CREATED, LocalDateTime.now(), savedDriverDto);
    }

    @Override
    public ApiResponseDto<DriverProfileDto> getDriverById(Long id) {
        logger.info("Fetching driver details for ID: {}", id);
        Optional<Driver> driver = driverRepository.findById(id);
        if (driver.isEmpty()) {
            logger.warn("Driver with ID {} does not exist.", id);
            throw new DriverDoesNotExistException("Driver does Not Exist");
        }
        DriverProfileDto driverProfile = modelMapper.map(driver.get(), DriverProfileDto.class);
        logger.info("Driver details successfully fetched for ID: {}", id);
        return new ApiResponseDto<>("Driver details Successfully fetched ", HttpStatus.OK, LocalDateTime.now(), driverProfile);
    }

    @Override
    public ApiResponseDto<Optional<Driver>> updateDriverStatus(Long id, Driver.DriverStatus status) {
        logger.info("Attempting to update driver status for ID {} to: {}", id, status);
        Optional<Driver> driverOptional = driverRepository.findById(id);
        if (driverOptional.isEmpty()) {
            logger.warn("Driver with ID {} does not exist for status update.", id);
            throw new DriverDoesNotExistException("Driver with ID " + id + " does not exist.");
        }
        driverOptional.map(driver1 -> {
            driver1.setStatus(status);
            logger.debug("Saving driver with ID {} with new status: {}", id, status);
            return driverRepository.save(driver1);
        });
        logger.info("Driver status updated successfully for ID {} to: {}", id, status);
        return new ApiResponseDto<>("Driver Changed to " + driverOptional.get().getStatus(), HttpStatus.OK, LocalDateTime.now(), null);
    }

    @Override
    public ApiResponseDto<List<AvailableDriverDto>> getAllAvailableDriver() {
        logger.info("Fetching all available and approved drivers.");
        List<Driver> driverList = driverRepository.findByStatusAndIsApproved(Driver.DriverStatus.AVAILABLE, true);
        if (driverList.isEmpty()) {
            logger.info("No available drivers found.");
            throw new DriverNotAvailableException("No Drivers Available");
        }
        logger.debug("Found {} available drivers.", driverList.size());
        List<AvailableDriverDto> availableDriverDtoList = driverList.stream()
                .map(driver -> modelMapper.map(driver, AvailableDriverDto.class))
                .collect(Collectors.toList());
        return new ApiResponseDto<>("Success", HttpStatus.OK, LocalDateTime.now(), availableDriverDtoList);
    }

    @Override
    public String rideComplete(Long id, Long rideID) {
        logger.info("Marking ride ID {} as COMPLETED for driver ID {}.", rideID, id);
        rideClient.updateRideStatus(rideID, RideDto.RideStatus.COMPLETED);
        logger.info("Ride status updated to COMPLETED for ride ID {}.", rideID);
        paymentInitiateClient.updatePaymentStatus(rideID, PaymentStatus.PENDING);
        logger.info("Payment status initiated to PENDING for ride ID {}.", rideID);
        return "Ride has been Completed";
    }

    @Override
    public ApiResponseDto<List<RideDto>> getDriverRideDetails(long driverId) {
        logger.info("Fetching ride details for driver ID: {}", driverId);
        List<RideDto> rideDetails = rideClient.getDriverRides(driverId);
        logger.info("Fetched {} rides for driver ID: {}", rideDetails.size(), driverId);
        return new ApiResponseDto<>("Success", HttpStatus.OK, LocalDateTime.now(), rideDetails);
    }

    @Override
    public ApiResponseDto<String> startRide(Long driverId) {
        logger.info("Attempting to start ride for driver ID: {}", driverId);
        Optional<currentDriverDto> driverRideDetailsOptional = rideClient.getLatestAssignedRideForDriver(driverId);

        if (driverRideDetailsOptional.isPresent()) {
            currentDriverDto rideDetails = driverRideDetailsOptional.get();
            logger.info("Found active ride ID {} for driver ID {}. User ID: {}, Actual Fare: {}",
                    rideDetails.getId(), rideDetails.getDriverId(), rideDetails.getUserId(), rideDetails.getActualFare());

            PaymentRequestDto paymentRequest = new PaymentRequestDto();
            paymentRequest.setRideId(rideDetails.getId());
            paymentRequest.setUserId(rideDetails.getUserId());
            paymentRequest.setDriverId(rideDetails.getDriverId());
            paymentRequest.setAmount((double) rideDetails.getActualFare());
            paymentRequest.setStatus(PaymentStatus.PENDING);
            paymentRequest.setPaymentMethod("UNDEFINED");
            logger.debug("Initiating payment for ride ID {}: {}", rideDetails.getId(), paymentRequest);

            ApiResponseDto<String> paymentResponse = paymentInitiateClient.initiatePayment(paymentRequest); // Cast to ApiResponseDto<String>
            logger.info("Payment initiation response for ride ID {}: Status - {}, Message - {}",
                    rideDetails.getId(), paymentResponse.getStatus(), paymentResponse.getMessage());

            rideClient.updateRideStatus(rideDetails.getId(), RideDto.RideStatus.RIDE_STARTED);
            logger.info("Ride status updated to RIDE_STARTED for ride ID {}.", rideDetails.getId());

            if (paymentResponse.getStatus() == HttpStatus.OK) {
                return new ApiResponseDto<>("Ride Started and Payment Initiation Successful", HttpStatus.OK, LocalDateTime.now(), null);
            } else {
                logger.error("Ride Started but Payment Initiation Failed for ride ID {}: {}", rideDetails.getId(), paymentResponse.getMessage());
                return new ApiResponseDto<>("Ride Started but Payment Initiation Failed: " + paymentResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), null);
            }

        } else {
            logger.warn("No active ride found for driver ID: {}", driverId);
            return new ApiResponseDto<>("No active ride found for driver", HttpStatus.NOT_FOUND, LocalDateTime.now(), null);
        }
    }



    @Transactional // Ensure the database update is atomic
    public boolean updateApprovalStatus(DriverApprovalUpdateDTO updateDTO) {
        logger.info("Attempting to update approval status for driver with license number: {}", updateDTO.getLicenseNumber());
        logger.debug("DriverApprovalUpdateDTO: {}", updateDTO);

        Optional<Driver> driverOptional = driverRepository.findByLicenseNumber(updateDTO.getLicenseNumber());

        if (driverOptional.isPresent()) {
            Driver driver = driverOptional.get();
            driver.setApproved(updateDTO.isApproved());
            driverRepository.save(driver); // Save the updated driver
            logger.info("Driver {} approval status updated to {} by admin {}", driver.getLicenseNumber(), updateDTO.isApproved(), updateDTO.getApprovedByAdminId());
            return true;
        } else {
            logger.warn("Attempted to update approval status for non-existent driver with license number: {}", updateDTO.getLicenseNumber());
            return false; // Driver not found
        }
    }
}