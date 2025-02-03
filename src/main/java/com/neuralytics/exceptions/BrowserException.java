package com.neuralytics.exceptions;

public class BrowserException extends RuntimeException {

    public BrowserException(String msg) {
        super(msg);
    }

    public BrowserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
