package com.software.modsen.ratingmicroservice.exceptions;

public class RatingSourceNotFoundException extends RuntimeException {
    public RatingSourceNotFoundException(String message) {
        super(message);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
