package com.hostel.management.common.exception;

import com.hostel.management.auth.exception.AccountNotActivatedException;
import com.hostel.management.auth.exception.InvalidCredentialsException;
import com.hostel.management.auth.exception.UserNotFoundException;
import com.hostel.management.common.response.ApiErrorResponse;
import com.hostel.management.student.exception.*;
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

    @ExceptionHandler(InactiveProgrammeException.class)
    public ResponseEntity<ApiErrorResponse> handleInactiveProgramme(InactiveProgrammeException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(400)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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



    @ExceptionHandler(StudentNotFoundException.class)
    public  ResponseEntity<ApiErrorResponse> handleStudentNotFound
            (StudentNotFoundException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(404)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleStudentAlreadyExists(StudentAlreadyExistsException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(409).message(ex.getMessage()).build();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ActivationCodeAlreadyGeneratedException.class)
    public ResponseEntity<ApiErrorResponse> handleActivationCodeAlreadyGenerated
            (ActivationCodeAlreadyGeneratedException ex)
    {
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(409)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ActivationCodeInvalidException.class)
        public ResponseEntity<ApiErrorResponse> handleActivationCodeInvalid
                (ActivationCodeInvalidException ex)
        {
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .message(ex.getMessage())
                    .status(400).build();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }

        @ExceptionHandler(StudentAlreadyActivatedException.class)
    public ResponseEntity<ApiErrorResponse> handleStudentAlreadyActivated
                (StudentAlreadyActivatedException ex)
        {
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .message(ex.getMessage())
                    .status(409).build();
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(error);
        }

        @ExceptionHandler(ActivationCodeExpired.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredCode
                (ActivationCodeExpired ex)
        {
            ApiErrorResponse error = ApiErrorResponse.builder()
                    .message(ex.getMessage())
                    .status(409).build();
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(error);
        }

        @ExceptionHandler(ActivationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleActivationNotFound(ActivationNotFoundException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(404)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(404)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExist(EmailAlreadyExistsException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(409)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex)
    {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(401)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotActivated(AccountNotActivatedException ex)
    {
        ApiErrorResponse error = ApiErrorResponse
                .builder()
                .status(403)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }






}
