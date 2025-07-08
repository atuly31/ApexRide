package com.cbs.Payment.dto;

import com.cbs.Payment.entity.Payment; // Import the outer class for the nested enum

import java.time.LocalDateTime;
import java.util.Objects; // Required for Objects.hash and Objects.equals

public class PaymentResponseDto {
    private Long id;
    private Long rideId;
    private Long userId;
    private Long driverId;
    private Double amount;
    private Payment.PaymentStatus status; // Use Payment.PaymentStatus for the nested enum
    private LocalDateTime paymentDate;
    private String transactionId;
    private String paymentMethod;
    private LocalDateTime lastUpdated;

    // No-argument constructor
    public PaymentResponseDto() {
    }

    // All-argument constructor
    public PaymentResponseDto(Long id, Long rideId, Long userId, Long driverId, Double amount,
                              Payment.PaymentStatus status, LocalDateTime paymentDate, String transactionId,
                              String paymentMethod, LocalDateTime lastUpdated) {
        this.id = id;
        this.rideId = rideId;
        this.userId = userId;
        this.driverId = driverId;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.lastUpdated = lastUpdated;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Payment.PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(Payment.PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // --- equals(), hashCode(), and toString() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentResponseDto that = (PaymentResponseDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(rideId, that.rideId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(driverId, that.driverId) &&
                Objects.equals(amount, that.amount) &&
                status == that.status &&
                Objects.equals(paymentDate, that.paymentDate) &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(paymentMethod, that.paymentMethod) &&
                Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rideId, userId, driverId, amount, status, paymentDate, transactionId, paymentMethod, lastUpdated);
    }

    @Override
    public String toString() {
        return "PaymentResponseDto{" +
                "id=" + id +
                ", rideId=" + rideId +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", amount=" + amount +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                ", transactionId='" + transactionId + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}