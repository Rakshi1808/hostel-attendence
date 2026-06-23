package com.hostel.management.student.controller;


import com.hostel.management.student.dto.ProgrammeCreateRequest;
import com.hostel.management.student.dto.ProgrammeResponse;
import com.hostel.management.student.service.ProgrammeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/programmes")
public class ProgrammeController
{
    private final ProgrammeService programmeService;

   public ProgrammeController(ProgrammeService programmeService)
    {
        this.programmeService = programmeService;
    }

    @PostMapping
    public ResponseEntity<ProgrammeResponse> createProgramme(@Valid @RequestBody ProgrammeCreateRequest programmeCreateRequest)
    {
        ProgrammeResponse response =  programmeService.createProgramme(programmeCreateRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
