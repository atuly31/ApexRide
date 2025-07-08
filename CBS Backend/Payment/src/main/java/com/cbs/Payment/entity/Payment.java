package com.cbs.Payment.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects; // Required for Objects.hash and Objects.equals

@Entity // Marks this class as a JPA entity, mapped to a database table
@Table(name = "payments") // Specifies the table name in the database
public class Payment {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures auto-incrementing ID
    private Long id;

    @Column(nullable = false) // Specifies that this column cannot be null
    private Long rideId; // Foreign key referencing the Ride ID from the Ride microservice

    @Column(nullable = false)
    private Long userId; // Foreign key referencing the User ID from the User microservice

    @Column(nullable = false)
    private Long driverId; // Foreign key referencing the Driver ID from the Driver microservice

    @Column(nullable = false)
    private Double amount; // The amount of the payment

    @Enumerated(EnumType.STRING) // Stores the enum name (string) in the database
    @Column(nullable = false)
    private PaymentStatus status; // Current status of the payment (e.g., PENDING, SUCCESS, FAILED)

    @Column(nullable = false)
    private LocalDateTime paymentDate; // Timestamp when the payment record was created

    private String transactionId; // Optional: To store a payment gateway transaction ID

    private String paymentMethod; // e.g., "Cash", "Card", "UPI"

    private LocalDateTime lastUpdated; // Timestamp for last update to the payment record

    // No-argument constructor
    public Payment() {
    }

    // All-argument constructor
    public Payment(Long id, Long rideId, Long userId, Long driverId, Double amount, PaymentStatus status,
                   LocalDateTime paymentDate, String transactionId, String paymentMethod, LocalDateTime lastUpdated) {
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
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

    // --- Lifecycle Callbacks ---
    @PrePersist // Callback method executed before the entity is first persisted
    protected void onCreate() {
        this.paymentDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        // Initial status is set when the payment record is created
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }

    @PreUpdate // Callback method executed before the entity is updated
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    // --- equals() and hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) &&
                Objects.equals(rideId, payment.rideId) &&
                Objects.equals(userId, payment.userId) &&
                Objects.equals(driverId, payment.driverId) &&
                Objects.equals(amount, payment.amount) &&
                status == payment.status &&
                Objects.equals(paymentDate, payment.paymentDate) &&
                Objects.equals(transactionId, payment.transactionId) &&
                Objects.equals(paymentMethod, payment.paymentMethod) &&
                Objects.equals(lastUpdated, payment.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rideId, userId, driverId, amount, status, paymentDate, transactionId, paymentMethod, lastUpdated);
    }

    // --- toString() ---
    @Override
    public String toString() {
        return "Payment{" +
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

    // --- Nested PaymentStatus Enum ---
    // Enum to define possible statuses for a payment
    public enum PaymentStatus {
        INITIATED,
        PENDING,    // Payment initiated but not yet confirmed
        SUCCESS,    // Payment successfully completed
        FAILED,     // Payment failed
        REFUNDED,   // Payment has been refunded
        CANCELLED   // Payment was cancelled before processing
    }
}