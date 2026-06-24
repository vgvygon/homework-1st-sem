package com.example.gateway.exception;

public class ExternalTimeoutException extends ExternalApiException {
    public ExternalTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
