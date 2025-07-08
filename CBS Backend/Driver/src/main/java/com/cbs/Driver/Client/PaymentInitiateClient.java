package com.cbs.Driver.Client;

import com.cbs.Driver.dto.ApiResponseDto;
import com.cbs.Driver.dto.PaymentRequestDto;
import com.cbs.Driver.dto.PaymentStatus;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "Payment")
public interface PaymentInitiateClient {

    @PostMapping("/api/v1/payments/initiate")
    ApiResponseDto initiatePayment(@Valid @RequestBody PaymentRequestDto requestDto);

    @PutMapping("api/v1/payments/{rideId}/status")
    void updatePaymentStatus(@PathVariable Long rideId, @RequestParam PaymentStatus newStatus);
}
