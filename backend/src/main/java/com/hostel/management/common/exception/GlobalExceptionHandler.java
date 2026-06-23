package com.hostel.management.common.exception;

import com.hostel.management.common.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException( RuntimeException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(409)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}
