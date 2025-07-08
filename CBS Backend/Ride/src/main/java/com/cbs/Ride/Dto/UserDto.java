package com.cbs.Ride.Dto;

import lombok.Data;

@Data
public class UserDto {
    public Long getId() {
        return id;
    }



    private Long id;
    private String firstName;
    private String lastName;
    private String email;

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


}
