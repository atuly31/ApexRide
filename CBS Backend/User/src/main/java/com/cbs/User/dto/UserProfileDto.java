package com.cbs.User.dto;

import java.time.LocalDateTime; // Import LocalDateTime

public class UserProfileDto {
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDateTime registrationDate; // Added to match test constructor
    private LocalDateTime lastProfileUpdate; // Added to match test constructor

    // Default (No-argument) constructor
    public UserProfileDto() {
    }

    // Constructor to match the unit test's instantiation:
    // new UserProfileDto(1L, "testUser", "test@example.com", "1234567890", "Test", "User", LocalDateTime.now(), LocalDateTime.now())
    public UserProfileDto(Long id, String userName, String email, String phoneNumber, String firstName, String lastName,
                          LocalDateTime registrationDate, LocalDateTime lastProfileUpdate) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationDate = registrationDate;
        this.lastProfileUpdate = lastProfileUpdate;
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public LocalDateTime getLastProfileUpdate() {
        return lastProfileUpdate;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setLastProfileUpdate(LocalDateTime lastProfileUpdate) {
        this.lastProfileUpdate = lastProfileUpdate;
    }
}