package com.cbs.Driver.Exception;

import feign.FeignException;

public class DriverServiceCommunicationException extends RuntimeException {
    private final Long driverIdAttempted;
    private final FeignException feignCause; // Keep the original FeignException if applicable

    public DriverServiceCommunicationException(String message, FeignException cause, Long driverIdAttempted) {
        super(message, cause);
        this.feignCause = cause;
        this.driverIdAttempted = driverIdAttempted;
    }

    public DriverServiceCommunicationException(String message, Throwable cause, Long driverIdAttempted) {
        super(message, cause);
        this.feignCause = (cause instanceof FeignException) ? (FeignException) cause : null;
        this.driverIdAttempted = driverIdAttempted;
    }

    public Long getDriverIdAttempted() {
        return driverIdAttempted;
    }

    public FeignException getFeignCause() {
        return feignCause;
    }
}
