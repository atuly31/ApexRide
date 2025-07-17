package com.cbs.User.dto;

public class UserRegistrationDto {
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String phoneNumber;

    // Default (No-argument) constructor - Essential for many frameworks (like Spring, JPA, Jackson)
    public UserRegistrationDto() {
    }

    // Constructor to match the unit test's instantiation:
    // new UserRegistrationDto("testUser", "password123", "test@example.com", "1234567890", "Test", "User")
    public UserRegistrationDto(String userName, String passwordHash, String email, String phoneNumber, String firstName, String lastName) {
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}