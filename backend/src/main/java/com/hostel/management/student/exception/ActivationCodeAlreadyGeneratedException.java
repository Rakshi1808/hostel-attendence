package com.hostel.management.student.exception;

public class ActivationCodeAlreadyGeneratedException extends RuntimeException {
    public ActivationCodeAlreadyGeneratedException(String message)
    {
        super(message);
    }
}
