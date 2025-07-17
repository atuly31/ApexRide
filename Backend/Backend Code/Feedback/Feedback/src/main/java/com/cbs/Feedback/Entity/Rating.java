package com.cbs.Feedback.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RatingID")
    private Long ratingId;

    @Column(name = "RideID", nullable = false)
    private Long rideId; // This will store the 'id' from the 'rides' table

    @Column(name = "UserID", nullable = false)
    private Long userId;

    @Column(name = "driverID", nullable = false)
    private Long driverId;

    @Column(name = "Score", nullable = false)
    private Integer score;

    @Column(name = "Comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "Timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // Constructors (add @Data from Lombok if used, or generate manually)
    public Rating() {
        this.timestamp = LocalDateTime.now();
    }

//    public Rating(Long rideId, Long fromUserId, Long toUserId, Integer score, String comments) {
//        this.rideId = rideId;
//        this.fromUserId = fromUserId;
//        this.toUserId = toUserId;
//        this.score = score;
//        this.comments = comments;
//        this.timestamp = LocalDateTime.now();
//    }

    // Getters and Setters
    public Long getRatingId() { return ratingId; }
    public void setRatingId(Long ratingId) { this.ratingId = ratingId; }
    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }

    public Long getUserId() { return userId; }
    public void setuserId(Long userId) { this.userId = userId; }
    public Long getdriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }




}
