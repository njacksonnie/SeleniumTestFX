package com.neuralytics.exceptions;

/**
 * Custom exception thrown when a wait condition is not met within the specified timeout.
 */
public class WaitTimeoutException extends RuntimeException {
    public WaitTimeoutException(String message) {
        super(message);
    }

    public WaitTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}