package com.software.modsen.ratingmicroservice.exceptions;

public class PassengerHasNotRatingsException extends RuntimeException {
    public PassengerHasNotRatingsException(String message) {
        super(message);
    }
}