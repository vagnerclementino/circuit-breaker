package me.clementino.apiproduct.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PriceServiceException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE_TEMPLATE = "Error on call price service. Status Code: %s. Message: %s";

    public PriceServiceException(int statusCode, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format(MESSAGE_TEMPLATE, statusCode, message));
    }

    public PriceServiceException(int statusCode, String message, Throwable cause) {
        super(HttpStatus.NOT_FOUND, String.format(MESSAGE_TEMPLATE, statusCode, message), cause);
    }


}
