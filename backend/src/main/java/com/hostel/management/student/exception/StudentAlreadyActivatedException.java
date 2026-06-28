package com.hostel.management.student.exception;

public class StudentAlreadyActivatedException extends RuntimeException
{
    public StudentAlreadyActivatedException(String message) {
        super(message);
    }
}
