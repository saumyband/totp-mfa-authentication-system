package com.saumya.authservice.exception;

public class MfaNotEnabledException extends RuntimeException {
    public MfaNotEnabledException(String message) {
        super(message);
    }
}
