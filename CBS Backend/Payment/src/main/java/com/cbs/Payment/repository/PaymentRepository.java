package com.cbs.Payment.repository;

import com.cbs.Payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marks this interface as a Spring Data JPA repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // JpaRepository provides methods like save(), findById(), findAll(), deleteById(), etc.

    // Custom query method to find a payment by its rideId
    // This will be useful when the Ride MS initiates payment and when the user pays
    Optional<Payment> findByRideId(Long rideId);
    Optional<Payment> findByUserIdAndStatus(Long userId, Payment.PaymentStatus status);
    // Optional: You might add more custom query methods if needed, e.g.:
    // List<Payment> findByUserId(Long userId);
    // List<Payment> findByDriverId(Long driverId);
    // List<Payment> findByStatus(PaymentStatus status);
}