package com.cbs.AuthService.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "authTable")
public class AuthEntity implements UserDetails {


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Using UUID for IDs
    private long id;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(unique = true, nullable = false)
    private String userName; // Can be email or unique identifier

    @NotBlank(message = "email number cannot be empty or null")
    @Email(message = "Email should be a valid email address")
    @Column(nullable = false, unique = true, length = 100)
    private String email;



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }




    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    @Column(nullable = false)
    private String passwordHash; // Storing hashed password



//    @Column(name = "entity_type", nullable = false)
//    private String entityType; // "USER" or "DRIVER"

    @Column(name = "entity_id")
    private long entityId; // ID from User Service's
//
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return this.getUserName();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public  String  role;



}
