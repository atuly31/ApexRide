package com.cbs.Driver.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.util.Objects; // Required for Objects.hash and Objects.equals

public class PaymentRequestDto {

    @NotNull(message = "Ride ID cannot be null")
    private Long rideId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Driver ID cannot be null")
    private Long driverId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus status; // Directly reference PaymentStatus enum

    private String paymentMethod; // e.g., "Cash", "Card", "UPI"

    // No-argument constructor
    public PaymentRequestDto() {
    }

    // All-argument constructor
    public PaymentRequestDto(Long rideId, Long userId, Long driverId, Double amount, PaymentStatus status, String paymentMethod) {
        this.rideId = rideId;
        this.userId = userId;
        this.driverId = driverId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    // --- Getters ---
    public Long getRideId() {
        return rideId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    // --- Setters ---
    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // --- equals(), hashCode(), and toString() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRequestDto that = (PaymentRequestDto) o;
        return Objects.equals(rideId, that.rideId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(driverId, that.driverId) &&
                Objects.equals(amount, that.amount) &&
                status == that.status &&
                Objects.equals(paymentMethod, that.paymentMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rideId, userId, driverId, amount, status, paymentMethod);
    }

    @Override
    public String toString() {
        return "PaymentRequestDto{" +
                "rideId=" + rideId +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", amount=" + amount +
                ", status=" + status +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}