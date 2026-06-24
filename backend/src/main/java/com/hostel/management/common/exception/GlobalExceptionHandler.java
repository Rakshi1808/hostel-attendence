package com.hostel.management.common.exception;

import com.hostel.management.common.response.ApiErrorResponse;
import com.hostel.management.student.exception.ProgrammeAlreadyExistsException;
import com.hostel.management.student.exception.ProgrammeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(ProgrammeNotFoundException.class)
    public  ResponseEntity<ApiErrorResponse> handleProgrammeNotFound
            (ProgrammeNotFoundException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(404)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(ProgrammeAlreadyExistsException.class)
    public  ResponseEntity<ApiErrorResponse> handleProgrammeAlreadyExists
            (ProgrammeAlreadyExistsException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(409)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex)
    {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .status(400)
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
