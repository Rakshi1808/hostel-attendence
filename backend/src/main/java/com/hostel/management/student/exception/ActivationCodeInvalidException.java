package com.hostel.management.student.exception;

public class ActivationCodeInvalidException extends RuntimeException {
    public ActivationCodeInvalidException(String message) {
        super(message);
    }
}
