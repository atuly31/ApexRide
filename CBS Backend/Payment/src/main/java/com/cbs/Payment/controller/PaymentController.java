package com.cbs.Payment.controller;

import com.cbs.Payment.dto.ApiResponseDto;
import com.cbs.Payment.dto.PaymentRequestDto;
import com.cbs.Payment.dto.PaymentResponseDto;
import com.cbs.Payment.entity.Payment;
import com.cbs.Payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Initiates a new payment for a ride.
     * This endpoint is typically called by the User Microservice after a ride is booked.
     *
     * @param requestDto The PaymentRequestDto containing details for the new payment.
     * @return A ResponseEntity containing an ApiResponseDto with the initiated payment details.
     */
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponseDto<PaymentResponseDto>> initiatePayment(
            @Valid @RequestBody PaymentRequestDto requestDto) {
        ApiResponseDto<PaymentResponseDto> response = paymentService.initiatePayment(requestDto);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{rideId}/status")
    public ResponseEntity<ApiResponseDto<PaymentResponseDto>> updatePaymentStatus(
            @PathVariable Long rideId,
            @RequestParam Payment.PaymentStatus newStatus) {
        ApiResponseDto<PaymentResponseDto> response = paymentService.updatePaymentStatusByRideId(rideId, newStatus);
        return new ResponseEntity<>(response, response.getStatus());
    }


    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponseDto<PaymentResponseDto>> getPaymentDetails(
            @PathVariable Long paymentId) {
        ApiResponseDto<PaymentResponseDto> response = paymentService.getPaymentDetails(paymentId);
        return new ResponseEntity<>(response, response.getStatus());
    }


    @GetMapping("/ride/{rideId}")
    public ResponseEntity<ApiResponseDto<PaymentResponseDto>> getPaymentDetailsByRideId(
            @PathVariable Long rideId) {
        ApiResponseDto<PaymentResponseDto> response = paymentService.getPaymentDetailsByRideId(rideId);
        return new ResponseEntity<>(response, response.getStatus());
    }


    @PutMapping("/confirm/{userId}") // NEW ENDPOINT
    public ResponseEntity<ApiResponseDto<PaymentResponseDto>> confirmPayment(
            @PathVariable Long userId) {
        ApiResponseDto<PaymentResponseDto> response = paymentService.confirmPaymentByUser(userId);
        return new ResponseEntity<>(response, response.getStatus());
    }
}