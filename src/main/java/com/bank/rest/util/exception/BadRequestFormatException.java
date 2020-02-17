package com.bank.rest.util.exception;

public class BadRequestFormatException extends RuntimeException {
    public BadRequestFormatException(String message) {
        super(message);
    }
}
