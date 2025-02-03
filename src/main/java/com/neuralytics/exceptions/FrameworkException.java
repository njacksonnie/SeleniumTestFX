package com.neuralytics.exceptions;

/**
 * Custom runtime exception class for framework-specific errors.
 */
public class FrameworkException extends RuntimeException {

    /**
     * Constructs a new FrameworkException with the specified detail message.
     *
     * @param msg the detail message.
     */
    public FrameworkException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new FrameworkException with the specified detail message and cause.
     *
     * @param msg the detail message.
     * @param cause the cause of the exception.
     */
    public FrameworkException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
