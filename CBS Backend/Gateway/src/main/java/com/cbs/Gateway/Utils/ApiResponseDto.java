package com.cbs.Gateway.Utils;

public class ApiResponseDto <T>{
    private boolean error;
    private String message;
    private int status;
    private T data;

    public ApiResponseDto(boolean error, String message, int status, T data) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    // Standard getters for serialization (Jackson uses these to build JSON)
    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }


    public T getData() {
        return data;
    }
}
