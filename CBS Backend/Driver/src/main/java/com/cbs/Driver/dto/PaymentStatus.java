package com.cbs.Driver.dto;

public enum PaymentStatus {
    INITIATED,      // Payment record created, waiting for further action
    PENDING, // Payment is being processed/awaited from user
    SUCCESSFUL,     // Payment completed successfully
    FAILED,         // Payment failed (e.g., card declined, cancellation)
    REFUNDED,       // Payment has been refunded
    CANCELLED       // Payment was cancelled (similar to FAILED in some contexts)
}
