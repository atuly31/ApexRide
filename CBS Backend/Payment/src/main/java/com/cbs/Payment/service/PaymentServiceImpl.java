package com.cbs.Payment.service;

import com.cbs.Payment.Client.DriverClient;
import com.cbs.Payment.Client.RideFeignClient;
import com.cbs.Payment.dto.ApiResponseDto;
import com.cbs.Payment.dto.PaymentRequestDto;
import com.cbs.Payment.dto.PaymentResponseDto;
import com.cbs.Payment.entity.Payment;
import com.cbs.Payment.repository.PaymentRepository;
import com.cbs.Payment.dto.DriverStatus;
import com.cbs.Payment.dto.RideStatus;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import feign.FeignException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final DriverClient driverFeignClient;
    private final RideFeignClient rideFeignClient;

    public PaymentServiceImpl(PaymentRepository paymentRepository, ModelMapper modelMapper,
                              DriverClient driverFeignClient, RideFeignClient rideFeignClient) {
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.driverFeignClient = driverFeignClient;
        this.rideFeignClient = rideFeignClient;
    }

    @Override
    public ApiResponseDto<PaymentResponseDto> initiatePayment(PaymentRequestDto requestDto) {

        Optional<Payment> existingPayment = paymentRepository.findByRideId(requestDto.getRideId());
        if (existingPayment.isPresent()) {
            log.warn("Payment already exists for ride ID: {}. Returning conflict status.", requestDto.getRideId());
            PaymentResponseDto responseDto = modelMapper.map(existingPayment.get(), PaymentResponseDto.class);
            return new ApiResponseDto<>("Payment already initiated for ride ID: ",HttpStatus.CONFLICT,LocalDateTime.now(),responseDto);
        }


        Payment payment = modelMapper.map(requestDto, Payment.class);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.INITIATED);
        payment.setTransactionId(UUID.randomUUID().toString());
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment initiated for ride ID: {} with Payment ID: {}", requestDto.getRideId(), savedPayment.getId());

        PaymentResponseDto responseDto = modelMapper.map(savedPayment, PaymentResponseDto.class);
        return new ApiResponseDto<>("Payment initiated successfully.",HttpStatus.OK,LocalDateTime.now(),responseDto);
        // --- END OF UPDATED BLOCK ---
    }

    @Override
    public ApiResponseDto<PaymentResponseDto> updatePaymentStatusByRideId(Long rideId, Payment.PaymentStatus newStatus) {

        Optional<Payment> optionalPayment = paymentRepository.findByRideId(rideId);
        if (optionalPayment.isEmpty()) {
            return new ApiResponseDto<>("Payment record not found for ride ID: "+ rideId,HttpStatus.NOT_FOUND,LocalDateTime.now(),null);
//            return ApiResponseDto.error("Payment record not found for ride ID: " + rideId, HttpStatus.NOT_FOUND);
        }
        Payment payment = optionalPayment.get();

        // Ensure proper status progression
        if (newStatus == Payment.PaymentStatus.PENDING && payment.getStatus() != Payment.PaymentStatus.INITIATED) {
            log.warn("Invalid status transition for ride {}: Cannot set to PAYMENT_PENDING from {}", rideId, payment.getStatus());
            return new ApiResponseDto<>("Invalid status transition: Cannot set to PAYMENT_PENDING from current status "+ rideId,HttpStatus.BAD_REQUEST,LocalDateTime.now(),null);

        }
        if (newStatus == Payment.PaymentStatus.FAILED && (payment.getStatus() == Payment.PaymentStatus.SUCCESS || payment.getStatus() == Payment.PaymentStatus.REFUNDED)) {
            log.warn("Invalid status transition for ride {}: Cannot set to FAILED from {}", rideId, payment.getStatus());
            return new ApiResponseDto<>("Invalid status transition: Cannot set to FAILED from current status " + payment.getStatus(),HttpStatus.BAD_REQUEST,LocalDateTime.now(),null);

        }
        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS || payment.getStatus() == Payment.PaymentStatus.FAILED || payment.getStatus() == Payment.PaymentStatus.REFUNDED) {
            log.warn("Attempt to update status of a final-state payment for ride {}. Current status: {}", rideId, payment.getStatus());
            return new ApiResponseDto<>("Cannot update status of a final-state payment (SUCCESSFUL, FAILED, REFUNDED). Current status:" + payment.getStatus(),HttpStatus.BAD_REQUEST,LocalDateTime.now(),null);
        }

        payment.setStatus(newStatus);
        payment.setPaymentDate(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.save(payment);

        PaymentResponseDto responseDto = modelMapper.map(updatedPayment, PaymentResponseDto.class);
        return new ApiResponseDto<>("Payment status updated successfully.",HttpStatus.OK,LocalDateTime.now(),responseDto);

    }

    @Override
    public ApiResponseDto<PaymentResponseDto> getPaymentDetails(Long paymentId) {
        // ... (this method remains the same) ...
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isEmpty()) {
            return new ApiResponseDto<>("Payment not found with ID: " + paymentId,HttpStatus.NOT_FOUND,LocalDateTime.now());

        }
        Payment payment = optionalPayment.get();
        PaymentResponseDto responseDto = modelMapper.map(payment, PaymentResponseDto.class);
        return new ApiResponseDto<>("Payment details retrieved.",HttpStatus.OK,LocalDateTime.now(),responseDto);

    }

    @Override
    public ApiResponseDto<PaymentResponseDto> getPaymentDetailsByRideId(Long rideId) {

        Optional<Payment> optionalPayment = paymentRepository.findByRideId(rideId);
        if (optionalPayment.isEmpty()) {
            return new  ApiResponseDto<>("Payment not found for ride ID: " + rideId,HttpStatus.NOT_FOUND,LocalDateTime.now());

        }
        Payment payment = optionalPayment.get();
        PaymentResponseDto responseDto = modelMapper.map(payment, PaymentResponseDto.class);
        return new ApiResponseDto<>("Payment details for ride retrieved.", HttpStatus.OK, LocalDateTime.now(),responseDto);
    }

    @Override
    public ApiResponseDto<PaymentResponseDto> confirmPaymentByUser(Long userId) {
        log.info("Attempting to confirm payment for User ID: {}", userId);

        // Find the pending payment for the given user ID
        Optional<Payment> optionalPayment = paymentRepository.findByUserIdAndStatus(userId, Payment.PaymentStatus.PENDING);

        if (optionalPayment.isEmpty()) {
            log.error("No pending payment found for User ID: {}", userId);
            return new ApiResponseDto<>("No pending payment found for User ID: " + userId, HttpStatus.NOT_FOUND, LocalDateTime.now());
        }

        Payment payment = optionalPayment.get();
        Long paymentId = payment.getId(); // Get paymentId for logging and further processing
        log.info("Found pending payment with ID: {} for User ID: {}", paymentId, userId);


        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            log.warn("Payment ID {} is not in PENDING status. Current status: {}", paymentId, payment.getStatus());
            return new ApiResponseDto<>("Payment cannot be confirmed from current status: " + payment.getStatus().name() + ". Expected: PENDING.", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment ID {} status updated to SUCCESS.", paymentId);


        Long driverId = updatedPayment.getDriverId();
//        Long driverId = 3L;
        if (driverId != null) {
            try {
                  driverFeignClient.updateDriverStatus(driverId, DriverStatus.AVAILABLE);
//                if (driverResponse.getStatus().is2xxSuccessful()) {
//                    log.info("Driver ID {} status successfully updated to AVAILABLE. Response: {}", driverId, driverResponse.getMessage());
//                } else {
//                    log.warn("Failed to update Driver ID {} status to AVAILABLE. Driver MS response: {}", driverId, driverResponse.getMessage());
//                }
            } catch (FeignException e) {
                log.error("Feign client error updating Driver ID {} status: {}", driverId, e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected error calling Driver MS for ID {}: {}", driverId, e.getMessage(), e);
            }
        } else {
            log.warn("No driverId found for payment ID {} to update driver status.", paymentId);
        }


        PaymentResponseDto responseDto = modelMapper.map(updatedPayment, PaymentResponseDto.class);

        return new ApiResponseDto<>("Payment confirmed successfully.", HttpStatus.OK, LocalDateTime.now(), null);
    }
}