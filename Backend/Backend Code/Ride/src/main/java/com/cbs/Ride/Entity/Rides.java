package com.cbs.Ride.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@Entity
@Table(name = "rides")
public class Rides {


//    public Rides(Long userId, String pickupLocation, String dropoffLocation, LocalDateTime startTime) {
//        this.userId = userId;
//        this.pickupLocation = pickupLocation;
//        this.dropoffLocation = dropoffLocation;
//        this.startTime = startTime;
//
//    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long driverId;

    @Column
    private String duration;

    public Rides() {

    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    private Float distance;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String dropoffLocation;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private float actualFare;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private int rating;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Enumerated(EnumType.STRING)
    public  RideStatus status;

    @Override
    public String toString() {
        return "Rides{" +
                "id=" + id +
                ", userId=" + userId +
                ", driverId=" + driverId +
                ", duration='" + duration + '\'' +
                ", distance=" + distance +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", dropoffLocation='" + dropoffLocation + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", actualFare=" + actualFare +
                ", rating=" + rating +
                ", status=" + status +
                '}';
    }

    public enum RideStatus {
        PENDING,          // User has requested a ride, but no driver search has started.
        SEARCHING_DRIVER, // The system is actively looking for an available driver.
        DRIVER_ASSIGNED,  // A driver has accepted the ride, but not yet started.
        RIDE_STARTED,          // The driver has picked up the passenger and the ride is in progress.
        COMPLETED,        // The ride has finished, passenger dropped off.
        CANCELLED_BY_USER, // User cancelled the ride.
        CANCELLED_BY_DRIVER, // Driver cancelled the ride.
        NO_DRIVER_FOUND,  // No driver could be found for the ride request.

    }
    public RideStatus getStatus() {
        return status;
    }
    public void setStatus(RideStatus status) {
        this.status = status;
    }


    public Long getDriverId() {
        return driverId;
    }
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }
    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public float getActualFare() {
        return actualFare;
    }
    public void setActualFare(float actualFare) {
        this.actualFare = actualFare;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }






}
