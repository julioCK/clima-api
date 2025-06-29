package com.juliock.exceptions;

public class ApiRequestLimitReachedException extends RuntimeException {
    public ApiRequestLimitReachedException(String message) {
        super(message);
    }
}
