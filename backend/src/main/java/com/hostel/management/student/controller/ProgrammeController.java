package com.hostel.management.student.controller;


import com.hostel.management.student.dto.ProgrammeCreateRequest;
import com.hostel.management.student.dto.ProgrammeResponse;
import com.hostel.management.student.service.ProgrammeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @GetMapping
    public List<ProgrammeResponse> getProgrammes()
    {
        return programmeService.getProgrammes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgrammeResponse> getProgrammeById(@PathVariable int id)
    {
        return ResponseEntity.ok(programmeService.getProgrammeById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgramme(@PathVariable int id)
    {
        programmeService.deleteProgramme(id);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllProgrammes()
    {
        programmeService.deleteAllProgrammes();
        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgrammeResponse> updateProgramme(@PathVariable int id,@Valid @RequestBody ProgrammeCreateRequest updateRequest)
    {
        ProgrammeResponse response = programmeService.updateProgramme(id,updateRequest);

        return ResponseEntity.ok(response);
    }

}
