package com.cbs.AuthService.Service;

import com.cbs.AuthService.AuthDto.ApiResponseDto;
import com.cbs.AuthService.AuthDto.AuthProfileUpdateDto;
import com.cbs.AuthService.AuthDto.DriverRegistrationDto;
import com.cbs.AuthService.AuthDto.UserRegistrationDto;
import com.cbs.AuthService.Client.DriverClient;
import com.cbs.AuthService.Client.UserClient;
import com.cbs.AuthService.Entity.AuthEntity;
import com.cbs.AuthService.Exception.AlreadyExistException;
import com.cbs.AuthService.Exception.DoesNotExistException;
import com.cbs.AuthService.Repository.AuthRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    PasswordEncoder passwordEncoder;
    private static  ModelMapper modelMapper;
    private static AuthRepository authRepository;
    @Autowired
    private  UserClient userClient;
    @Autowired
    private DriverClient driverClient;
    @Autowired
    public RegistrationServiceImpl(ModelMapper modelMapper , AuthRepository authRepository){
        RegistrationServiceImpl.modelMapper = modelMapper;
        RegistrationServiceImpl.authRepository= authRepository;

    }

    @Override
    public ApiResponseDto<AuthEntity> registerUser(UserRegistrationDto registrationDto) {

        if (authRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new AlreadyExistException("Email Already Exist");
        }

        AuthEntity authEntity = modelMapper.map(registrationDto, AuthEntity.class);
        authEntity.setRole("User");
        authEntity.setPasswordHash(passwordEncoder.encode(registrationDto.getPasswordHash()));

        AuthEntity savedAuthEntity = null; // Declare outside try-catch for broader scope

        try {
            savedAuthEntity = authRepository.save(authEntity);
            ApiResponseDto<UserRegistrationDto> responseFromUser = userClient.addUser(registrationDto);

            if (responseFromUser.getData() != null && responseFromUser.getData().getId() != null
                    && (responseFromUser.getStatus().equals(HttpStatus.CREATED) || responseFromUser.getStatus().equals(HttpStatus.OK))) {

                savedAuthEntity.setEntityId(responseFromUser.getData().getId());
                AuthEntity finalAuthEntity = authRepository.save(savedAuthEntity); // Save again to update entityId
                return new ApiResponseDto<>("Success", HttpStatus.CREATED, LocalDateTime.now(), finalAuthEntity);
            } else {
                System.err.println("User service registration failed with status: " + responseFromUser.getStatus() +
                        " and message: " + responseFromUser.getMessage());


                if (savedAuthEntity != null && savedAuthEntity.getId() != -1l) {
                    authRepository.delete(savedAuthEntity);
                }
                return new ApiResponseDto<>("User registration failed: " + responseFromUser.getMessage(),
                        responseFromUser.getStatus(), LocalDateTime.now(), null);
            }

        } catch (feign.FeignException fe) {
            System.err.println("Feign Client Error calling User Service: " + fe.getMessage());
            if (savedAuthEntity != null && savedAuthEntity.getId() != -1l) {
                authRepository.delete(savedAuthEntity);
            }

            if (fe.status() == HttpStatus.CONFLICT.value()) {
                throw new AlreadyExistException("User with provided details already exists in User Service.");
            }
            return new ApiResponseDto<>("Failed to register user due to communication error: " + fe.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), null);

        } catch (DataIntegrityViolationException e) {
            if (savedAuthEntity != null && savedAuthEntity.getId() != -1l) {
                authRepository.delete(savedAuthEntity);
            }
            if (e.getMostSpecificCause() != null && e.getMostSpecificCause().getMessage() != null) {
                if (e.getMostSpecificCause().getMessage().contains("gmail") || e.getMostSpecificCause().getMessage().contains("email")) {
                    throw new AlreadyExistException("Email Already Exist");
                } else if (e.getMostSpecificCause().getMessage().contains("Duplicate entry")) {
                    throw new AlreadyExistException("Phone Number Already Exist or other duplicate entry in Auth Service");
                }
            }
            return new ApiResponseDto<>("Failed to register user due to data conflict in Auth Service.",
                    HttpStatus.BAD_REQUEST, LocalDateTime.now(), null);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during user registration: " + e.getMessage());
            if (savedAuthEntity != null && savedAuthEntity.getId() != -1l) {
                authRepository.delete(savedAuthEntity);
            }
            return new ApiResponseDto<>("An internal error occurred during user registration: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), null);
        }
    }


    public ApiResponseDto<AuthEntity> registerDriver(DriverRegistrationDto registrationDto) {
        Logger log = LoggerFactory.getLogger(getClass());

        log.info("Attempting to register driver with email: {}", registrationDto.getEmail());
        log.debug("Registration DTO received: {}", registrationDto);

        if (authRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            log.warn("Registration failed: Email '{}' already exists.", registrationDto.getEmail());
            throw new AlreadyExistException("Email Already Exists");
        }
        log.debug("Email '{}' is not found, proceeding with registration.", registrationDto.getEmail());

        AuthEntity authEntity = modelMapper.map(registrationDto, AuthEntity.class);
        authEntity.setRole("Driver");
        authEntity.setPasswordHash(passwordEncoder.encode(registrationDto.getPasswordHash()));
        log.debug("Mapped AuthEntity before initial save: {}", authEntity);

        AuthEntity savedAuthEntity = null;

        try {
            savedAuthEntity = authRepository.save(authEntity);
            log.info("AuthEntity saved initially with ID: {}", savedAuthEntity.getId());
            log.debug("Saved AuthEntity details: {}", savedAuthEntity);

            log.info("Calling Driver Microservice to add driver for license number: {}", registrationDto.getLicenseNumber());
            ApiResponseDto<DriverRegistrationDto> responseFromDriver = driverClient.addDriver(registrationDto);
            log.info("Received response from Driver Microservice. Status: {}, Message: {}",
                    responseFromDriver.getStatus(), responseFromDriver.getMessage());
            log.debug("Full response from Driver Microservice: {}", responseFromDriver);

            if (responseFromDriver.getData() != null && responseFromDriver.getData().getId() != null
                    && (responseFromDriver.getStatus().equals(HttpStatus.CREATED) || responseFromDriver.getStatus().equals(HttpStatus.OK))) {
                Long driverId = responseFromDriver.getData().getId();
                log.info("Driver registration successful. Driver ID received: {}. Updating AuthEntity with this ID.", driverId);
                savedAuthEntity.setEntityId(driverId);
                AuthEntity finalAuthEntity = authRepository.save(savedAuthEntity);
                log.info("AuthEntity updated with entityId {}. Driver registration process complete.", driverId);
                return new ApiResponseDto<>("Success", HttpStatus.CREATED, LocalDateTime.now(), finalAuthEntity);
            } else {
                log.error("Driver service registration failed or returned unexpected status/data. Status: {}, Message: {}",
                        responseFromDriver.getStatus(), responseFromDriver.getMessage());
                log.debug("Response data from driver service: {}", responseFromDriver.getData());

                // **Compensation: Delete AuthEntity if Driver service reports failure**
                if (savedAuthEntity != null && savedAuthEntity.getId() != -1L && savedAuthEntity.getId() != -1L) {
                    log.warn("Deleting AuthEntity with ID {} due to failed driver service registration response.", savedAuthEntity.getId());
                    authRepository.delete(savedAuthEntity);
                    log.info("AuthEntity with ID {} deleted successfully.", savedAuthEntity.getId());
                } else {
                    log.warn("AuthEntity was null or had invalid ID after initial save, cannot delete.");
                }
                return new ApiResponseDto<>("Driver registration failed: " + responseFromDriver.getMessage(),
                        HttpStatus.BAD_REQUEST, LocalDateTime.now(), null);
            }

        } catch (feign.FeignException fe) {
            log.error("Feign Client Error calling Driver Service for email '{}': {}", registrationDto.getEmail(), fe.getMessage(), fe);
            // **Compensation: Delete AuthEntity on Feign error (already implemented)**
            if (savedAuthEntity != null && savedAuthEntity.getId() != -1L && savedAuthEntity.getId() != -1L) {
                log.warn("Deleting AuthEntity with ID {} due to Feign client error.", savedAuthEntity.getId());
                authRepository.delete(savedAuthEntity);
                log.info("AuthEntity with ID {} deleted successfully after Feign error.", savedAuthEntity.getId());
            } else {
                log.warn("AuthEntity was null or had invalid ID after initial save, cannot delete after Feign error.");
            }
            // Rethrow the specific FeignException for the GlobalExceptionHandler to process
            // or wrap it in a custom exception if you need to add more context.
            throw fe; // Let the GlobalExceptionHandler handle this.

        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException occurred during driver registration for email '{}': {}", registrationDto.getEmail(), e.getMessage(), e);

            // **Compensation: Delete AuthEntity on DataIntegrityViolation (already implemented)**
            if (savedAuthEntity != null && savedAuthEntity.getId() != -1L && savedAuthEntity.getId() != -1L) {
                log.warn("Attempting to delete AuthEntity with ID {} due to DataIntegrityViolationException.", savedAuthEntity.getId());
                try {
                    authRepository.delete(savedAuthEntity);
                    log.info("AuthEntity with ID {} successfully deleted after DataIntegrityViolation.", savedAuthEntity.getId());
                } catch (Exception deleteEx) {
                    log.error("Failed to delete AuthEntity with ID {} after DataIntegrityViolation: {}", savedAuthEntity.getId(), deleteEx.getMessage(), deleteEx);
                }
            } else {
                log.warn("AuthEntity was null or had invalid ID, cannot delete after DataIntegrityViolationException.");
            }

            if (e.getMostSpecificCause() != null && e.getMostSpecificCause().getMessage() != null) {
                String causeMessage = e.getMostSpecificCause().getMessage();
                log.error("Most specific cause of DataIntegrityViolation: {}", causeMessage);

                if (causeMessage.toLowerCase().contains("duplicate entry") && causeMessage.toLowerCase().contains("for key")) {
                    if (causeMessage.toLowerCase().contains("email")) {
                        log.warn("Duplicate email detected via DB constraint: {}", registrationDto.getEmail());
                        throw new AlreadyExistException("Email Already Exist (DB Constraint)");
                    } else if (causeMessage.toLowerCase().contains("phone") || causeMessage.toLowerCase().contains("phonenumber")) {
                        log.warn("Duplicate phone number detected via DB constraint for email: {}", registrationDto.getEmail());
                        throw new AlreadyExistException("Phone Number Already Registered");
                    } else {
                        log.warn("Unhandled duplicate entry for email '{}': {}", registrationDto.getEmail(), causeMessage);
                        throw new AlreadyExistException("Duplicate entry detected: " + causeMessage);
                    }
                } else if (causeMessage.toLowerCase().contains("column") && causeMessage.toLowerCase().contains("cannot be null")) {
                    log.warn("NOT NULL constraint violation for email '{}': {}", registrationDto.getEmail(), causeMessage);
                    throw new IllegalArgumentException("Missing required data: " + causeMessage);
                }
                // For other DataIntegrityViolation scenarios, you might want to return a generic error or rethrow a custom one.
                return new ApiResponseDto<>("Failed to register driver due to data conflict: " + causeMessage,
                        HttpStatus.BAD_REQUEST, LocalDateTime.now(), null);
            } else {
                return new ApiResponseDto<>("Failed to register driver due to data conflict.",
                        HttpStatus.BAD_REQUEST, LocalDateTime.now(), null);
            }

        } catch (Exception e) {
            log.error("An unexpected error occurred during driver registration for email '{}': {}", registrationDto.getEmail(), e.getMessage(), e);
            // **Compensation: Delete AuthEntity on unexpected error (already implemented)**
            if (savedAuthEntity != null && savedAuthEntity.getId() != -1L && savedAuthEntity.getId() != -1L) {
                log.warn("Deleting AuthEntity with ID {} due to unexpected error.", savedAuthEntity.getId());
                authRepository.delete(savedAuthEntity);
                log.info("AuthEntity with ID {} deleted successfully after unexpected error.", savedAuthEntity.getId());
            }
            return new ApiResponseDto<>("An unexpected error occurred during driver registration.",
                    HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), null);
        }
    }

    @Override
    public AuthProfileUpdateDto updateUserProfile(long userId, AuthProfileUpdateDto authProfileUpdateDto) throws Exception {

        AuthEntity authUser = authRepository.findByEntityId(userId)
                .orElseThrow(() -> new DoesNotExistException("User not found in Auth service database"));
        authUser.setUserName(authProfileUpdateDto.getUserName());
        authUser.setEmail(authProfileUpdateDto.getEmail());
        authRepository.save(authUser);

        return modelMapper.map(authUser,AuthProfileUpdateDto.class);
    }


    }


