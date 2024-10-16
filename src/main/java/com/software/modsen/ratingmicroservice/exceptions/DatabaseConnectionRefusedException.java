package com.software.modsen.ratingmicroservice.exceptions;

public class DatabaseConnectionRefusedException extends RuntimeException {
    public DatabaseConnectionRefusedException(String message) {
        super(message);
    }
}