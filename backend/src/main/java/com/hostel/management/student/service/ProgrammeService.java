package com.hostel.management.student.service;

import com.hostel.management.student.dto.ProgrammeCreateRequest;

import com.hostel.management.student.dto.ProgrammeResponse;
import com.hostel.management.student.entity.Programme;
import com.hostel.management.student.exception.ProgrammeAlreadyExistsException;
import com.hostel.management.student.exception.ProgrammeNotFoundException;
import com.hostel.management.student.repository.ProgrammeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProgrammeService
{
    private final ProgrammeRepository programmeRepository;

    ProgrammeService(ProgrammeRepository programmeRepository)
    {
        this.programmeRepository = programmeRepository;
    }

    // mapper function to reuse anywhere we want convert programme to response body
    private ProgrammeResponse mapToResponse(Programme programme)
    {
        return ProgrammeResponse.builder()
                .id(programme.getId())
                .programmeName(programme.getProgrammeName())
                .totalYears(programme.getTotalYears())
                .isActive(programme.getIsActive())
                .build();
    }

    public ProgrammeResponse createProgramme(ProgrammeCreateRequest request)
    {
        if(programmeRepository.existsByProgrammeName(request.getProgrammeName()))
        {

            throw new ProgrammeAlreadyExistsException(
                    "Programme already exists"
            );
        }

        Programme programme = Programme.builder()
                .programmeName(request.getProgrammeName())
                .totalYears(request.getTotalYears())
                .isActive(true)
                .build();

        Programme programmeSave = programmeRepository.save(programme);

        return mapToResponse(programmeSave);


    }

    public List<ProgrammeResponse> getProgrammes()
    {
        List<Programme> programmes = programmeRepository.findAll();
        List<ProgrammeResponse> responses = new ArrayList<>();


        for(Programme programme : programmes)
        {
            responses.add(mapToResponse(programme));
        }
        return responses;
    }

    public void deleteProgramme(int id)
    {
        if(!programmeRepository.existsById(id))
        {
            throw new ProgrammeNotFoundException("Programme Not Found!");
        }
        programmeRepository.deleteById(id);

    }

    public void deleteAllProgrammes()
    {
        programmeRepository.deleteAll();
    }

    public ProgrammeResponse getProgrammeById(int id)
    {
        Programme programme = programmeRepository.findById(id)
                .orElseThrow(() -> new ProgrammeNotFoundException("Program not found"));

        return mapToResponse(programme);
    }

    public ProgrammeResponse updateProgramme(int  id, ProgrammeCreateRequest updateRequest)
    {
        Programme programme = programmeRepository.findById(id)
                .orElseThrow(() -> new ProgrammeNotFoundException("Program Not Found"));

        if(!programme.getProgrammeName().equals(updateRequest.getProgrammeName()))
        {
            if(programmeRepository.existsByProgrammeName(updateRequest.getProgrammeName()))
                throw new ProgrammeAlreadyExistsException("Programme Name already exists");
            else{
                programme.setProgrammeName(updateRequest.getProgrammeName());
            }
        }

        programme.setTotalYears(updateRequest.getTotalYears());
        Programme saveProgramme = programmeRepository.save(programme);
    return mapToResponse(saveProgramme);
    }
}
