package com.cbs.User.Entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Table(name = "users")
public class User   {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3,max = 20, message = "Name must be between 3 and 20 digits")
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Name number cannot be empty or null")
    private String firstName;

    @Size(min = 3,max = 20, message = "Name must be between 3 and 20 digits")
    @NotBlank(message = "lastName number cannot be empty or null")
    @Column(nullable = false, length = 50)
    private String lastName;


    @NotBlank(message = "email number cannot be empty or null")
    @Email(message = "Email should be a valid email address")
    @Column(nullable = false, unique = true, length = 100)
    private String email;


     @NotBlank(message = "Username number cannot be empty or null")
     @Column(nullable = false, unique = true, length = 100)
     private String userName;


    @Size(min = 10, max = 10, message = "Phone number must be between 10 and 20 digits")
    @NotBlank(message = "Phone number cannot be empty or null")
    @Column(nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private LocalDateTime lastProfileUpdate;


    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastProfileUpdate() {
        return lastProfileUpdate;
    }
    public void setLastProfileUpdate(LocalDateTime lastProfileUpdate) {
        this.lastProfileUpdate = lastProfileUpdate;
    }



}
