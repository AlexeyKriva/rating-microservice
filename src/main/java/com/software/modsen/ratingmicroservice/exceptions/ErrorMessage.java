package com.software.modsen.ratingmicroservice.exceptions;

public class ErrorMessage {
    public static final String RATING_NOT_FOUND_MESSAGE = "Rating not found.";
    public static final String RATING_SOURCE_NOT_FOUND_MESSAGE = "Rating source not found.";

    public static final String METHOD_NOT_SUPPORTED_MESSAGE = " method is not supported.";
    public static final String INVALID_TYPE_FOR_PARAMETER_MESSAGE = "Invalid value for parameter '%s'. Expected type:" +
            " %s, but got: %s.";
    public static final String JSON_MAPPING_MESSAGE = "Invalid data in JSON";
    public static final String REQUEST_RESOURCE_NOT_FOUND_MESSAGE = "The requested resource was not found. Please" +
            " check the URL and try again.";
    public static final String INVALID_JSON_FORMAT = "Invalid json format.";
    public static final String DATA_INTEGRITY_VIOLENT_MESSAGE = "Not all data entered.";
    public static final String FEIGN_CANNOT_CONNECT_MESSAGE = "Unsuccessful attempt to connect to the services for" +
            " rides, passenger or driver. ";
    public static final String MISSING_REQUIRED_PARAMETERS_MESSAGE = "Missing required parameters: ";

    public static final String PASSENGER_HAS_NOT_RATINGS_MESSAGE = "The passenger has not left any ratings yet.";
    public static final String DRIVER_HAS_NOT_RATINGS_MESSAGE = "The driver has not left any ratings yet.";

    public static final String BAD_CONNECTION_TO_DATABASE_MESSAGE = "Unsuccessful attempt to connect to the database. " +
            "Please, wait and try again later.";

    public static final String CANNOT_GET_DATA_MESSAGE = " For this reason you cannot get the data.";
    public static final String CANNOT_UPDATE_DATA_MESSAGE = " For this reason you cannot update the data.";
}