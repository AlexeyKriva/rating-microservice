package com.software.modsen.ratingmicroservice.exceptions;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}