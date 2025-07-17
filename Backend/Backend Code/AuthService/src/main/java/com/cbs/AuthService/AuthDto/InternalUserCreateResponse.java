package com.cbs.AuthService.AuthDto;

public class InternalUserCreateResponse {
    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private Long id; // The actual UUID from the User/Driver service's database
    private boolean success;
    private String message;
}
