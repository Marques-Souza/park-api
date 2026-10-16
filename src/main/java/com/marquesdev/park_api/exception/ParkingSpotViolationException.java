package com.marquesdev.park_api.exception;

public class ParkingSpotViolationException extends RuntimeException {
    public ParkingSpotViolationException(String message) {
        super(message);
    }
}
