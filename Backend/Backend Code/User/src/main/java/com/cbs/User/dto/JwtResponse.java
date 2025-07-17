package com.cbs.User.dto;

import lombok.Builder;

@Builder
public class JwtResponse {
    private String jwtToken;

    public JwtResponse(String jwtToken, String username) {
        this.jwtToken = jwtToken;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    private String username;
}
