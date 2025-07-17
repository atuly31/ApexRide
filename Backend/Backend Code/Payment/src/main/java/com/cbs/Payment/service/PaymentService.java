package com.cbs.Payment.service;

import com.cbs.Payment.dto.ApiResponseDto;
import com.cbs.Payment.dto.PaymentRequestDto;
import com.cbs.Payment.dto.PaymentResponseDto;
import com.cbs.Payment.entity.Payment;

public interface PaymentService {


    ApiResponseDto<PaymentResponseDto> initiatePayment(PaymentRequestDto requestDto);

    ApiResponseDto<PaymentResponseDto> updatePaymentStatusByRideId(Long rideId, Payment.PaymentStatus newStatus);

    ApiResponseDto<PaymentResponseDto> getPaymentDetails(Long paymentId);

    ApiResponseDto<PaymentResponseDto> getPaymentDetailsByRideId(Long rideId);

    ApiResponseDto<PaymentResponseDto> confirmPaymentByUser(Long paymentId);
}