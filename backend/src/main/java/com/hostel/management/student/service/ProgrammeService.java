package com.hostel.management.student.service;

import com.hostel.management.student.dto.ProgrammeCreateRequest;

import com.hostel.management.student.dto.ProgrammeResponse;
import com.hostel.management.student.entity.Programme;
import com.hostel.management.student.repository.ProgrammeRepository;
import org.springframework.stereotype.Service;

@Service
public class ProgrammeService
{
    private final ProgrammeRepository programmeRepository;

    ProgrammeService(ProgrammeRepository programmeRepository)
    {
        this.programmeRepository = programmeRepository;
    }

    public ProgrammeResponse createProgramme(ProgrammeCreateRequest request)
    {
        if(programmeRepository.existsByProgrammeName(request.getProgrammeName()))
        {

            throw new RuntimeException(
                    "Programme already exists"
            );
        }

        Programme programme = Programme.builder()
                .programmeName(request.getProgrammeName())
                .totalYears(request.getTotalYears())
                .isActive(true)
                .build();

        Programme programmeSave = programmeRepository.save(programme);

        return ProgrammeResponse.builder()
                .id(programmeSave.getId())
                .programmeName(programmeSave.getProgrammeName())
                .totalYears(programmeSave.getTotalYears())
                .isActive(programmeSave.getIsActive())
                .build();


    }
}
