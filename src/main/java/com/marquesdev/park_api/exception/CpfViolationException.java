package com.marquesdev.park_api.exception;

public class CpfViolationException extends RuntimeException {
    public CpfViolationException(String message) {
        super(message);
    }
}
