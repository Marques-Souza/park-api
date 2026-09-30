package com.marquesdev.park_api.exception;

public class UsernameUniqueException extends RuntimeException {
    public UsernameUniqueException(String message) {
        super(message);
    }
}
