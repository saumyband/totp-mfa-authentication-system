package com.saumya.userservice.exception;

public class MfaNotEnabledException extends RuntimeException {
    public MfaNotEnabledException(String message) {
        super(message);
    }
}
