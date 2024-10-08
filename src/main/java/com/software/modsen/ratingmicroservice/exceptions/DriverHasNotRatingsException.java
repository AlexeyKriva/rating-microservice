package com.software.modsen.ratingmicroservice.exceptions;

public class DriverHasNotRatingsException extends RuntimeException {
    public DriverHasNotRatingsException(String message) {
        super(message);
    }
}