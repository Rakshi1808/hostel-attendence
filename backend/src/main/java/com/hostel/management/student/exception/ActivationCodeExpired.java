package com.hostel.management.student.exception;

public class ActivationCodeExpired extends RuntimeException {
    public ActivationCodeExpired(String message) {
        super(message);
    }
}
