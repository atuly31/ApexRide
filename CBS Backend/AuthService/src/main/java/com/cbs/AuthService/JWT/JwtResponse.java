package com.cbs.AuthService.JWT;

public class JwtResponse {


    private String jwtToken;
    private String userName;

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private long entityId;
    public String getRole() {
        return role;
    }

    public JwtResponse(String jwtToken, String userName, long entityId, String role) {
        this.jwtToken = jwtToken;
        this.userName = userName;
        this.entityId = entityId;
        this.role = role;

    }

    public void setRole(String role) {
        this.role = role;
    }

    private String role;
    public JwtResponse(String jwtToken, String username,String role) {
        this.jwtToken = jwtToken;
        this.username = username;
        this.role=role;
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
