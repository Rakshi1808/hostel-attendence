package com.hostel.management.student.controller;

import com.hostel.management.student.dto.ActivationGenerateResponse;
import com.hostel.management.student.dto.StudentActivationRequest;
import com.hostel.management.student.dto.StudentCreateRequest;
import com.hostel.management.student.dto.StudentSummaryResponse;
import com.hostel.management.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController
{
    private final StudentService studentService;

    public StudentController(StudentService studentService)
    {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentSummaryResponse> createStudent(@Valid @RequestBody StudentCreateRequest studentCreateRequest)
    {
        StudentSummaryResponse studentSummaryResponse = studentService.createStudent(studentCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentSummaryResponse);
    }

    @PostMapping("/activation/{rollNo}")
    public ResponseEntity<ActivationGenerateResponse> generateActivationCode
            (@PathVariable String rollNo)
    {
        ActivationGenerateResponse activationGenerateResponse = studentService.generateActivationCode(rollNo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(activationGenerateResponse);
    }

    @PutMapping("/activation")
    public ResponseEntity<String> loginActivation
            (@Valid @RequestBody StudentActivationRequest studentActivationRequest)
    {
        String response = studentService.loginActivation(studentActivationRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


}
