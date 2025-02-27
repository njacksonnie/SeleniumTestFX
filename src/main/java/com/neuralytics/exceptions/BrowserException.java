package com.neuralytics.exceptions;

/**
 * A custom runtime exception for browser-related errors in a Selenium WebDriver
 * framework.
 * Extends {@link RuntimeException} to represent unrecoverable issues
 * encountered during browser
 * initialization or operation, such as invalid browser types, missing remote
 * hub URLs, or unsupported
 * browser configurations. This exception is typically thrown by components like
 * {@code DriverFactory}
 * when browser setup fails.
 *
 * <p>
 * Usage example:
 * 
 * <pre>
 * if (!isSupportedBrowser(browserName)) {
 *     throw new BrowserException("Unsupported browser: " + browserName);
 * }
 * </pre>
 *
 * <p>
 * As a runtime exception, it does not require explicit catching, but can be
 * handled if specific
 * recovery or logging is needed.
 *
 * @see RuntimeException
 */
public class BrowserException extends RuntimeException {

    /**
     * Constructs a new {@code BrowserException} with the specified error message.
     *
     * @param msg the detail message describing the browser-related error
     */
    public BrowserException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new {@code BrowserException} with the specified error message
     * and cause.
     *
     * @param msg   the detail message describing the browser-related error
     * @param cause the underlying throwable that caused this exception (e.g., an
     *              {@link IOException})
     */
    public BrowserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}