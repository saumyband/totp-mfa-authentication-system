package com.saumya.authservice.exception;

public class MfaAlreadyEnabledException extends RuntimeException {
    public MfaAlreadyEnabledException(String message) {
        super(message);
    }
}
