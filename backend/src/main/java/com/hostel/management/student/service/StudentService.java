package com.hostel.management.student.service;

import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.common.service.RandomCodeService;
import com.hostel.management.student.dto.ActivationGenerateResponse;
import com.hostel.management.student.dto.StudentActivationRequest;
import com.hostel.management.student.dto.StudentCreateRequest;
import com.hostel.management.student.dto.StudentSummaryResponse;
import com.hostel.management.student.entity.Programme;
import com.hostel.management.student.entity.Student;
import com.hostel.management.student.entity.StudentActivation;
import com.hostel.management.student.exception.*;
import com.hostel.management.student.repository.ProgrammeRepository;
import com.hostel.management.student.repository.StudentActivationRepository;
import com.hostel.management.student.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StudentService
{
    private final StudentRepository studentRepository;
    private final ProgrammeRepository programmeRepository;
    private final StudentActivationRepository activationRepository;
    private final RandomCodeService randomCodeService;
    private final PasswordEncoder passwordEncoder;
    public StudentService(StudentRepository studentRepository,ProgrammeRepository programmeRepository,
                          StudentActivationRepository activationRepository,RandomCodeService randomCodeService
                            ,PasswordEncoder passwordEncoder)
    {
        this.studentRepository = studentRepository;
        this.programmeRepository = programmeRepository;
        this.activationRepository = activationRepository;
        this.randomCodeService = randomCodeService;
        this.passwordEncoder = passwordEncoder;
    }

    private ActivationGenerateResponse mapToActivationResponse(StudentActivation create)
    {
        return ActivationGenerateResponse.builder()
                .activationCode(create.getActivationCode())
                .rollNo(create.getRollNo())
                .expiresAt(create.getExpiresAt())
                .build();
    }
    private Programme getProgramme(int id)
    {
        return programmeRepository.findById(id)
                .orElseThrow(() -> new ProgrammeNotFoundException("Program not Found!"));
    }

    private Student getStudent(String id)
    {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student Not Found!"));
    }


    public StudentSummaryResponse createStudent( StudentCreateRequest studentCreateRequest)
    {
        if(studentRepository.existsById(studentCreateRequest.getRollNo()))
        {
            throw new StudentAlreadyExistsException("Student already exists");
        }
        Programme programme = getProgramme(studentCreateRequest.getProgrammeId());

        if(!programme.getIsActive())
        {
            throw new InactiveProgrammeException("Programme "+ programme.getProgrammeName()+ " is inactive");
        }
        // building the student object that will save behind the database
        Student student = Student.builder()
                .rollNo(studentCreateRequest.getRollNo())
                .name(studentCreateRequest.getName())
                .studentPhone(studentCreateRequest.getStudentPhone())
                .yearOfStudy(studentCreateRequest.getYearOfStudy())
                .programme(programme)
                .accountStatus(AccountStatus.PENDING)
                .build();


        Student studentSaved = studentRepository.save(student);

                // building object and return to frontend this will show to frontend after creation
       return StudentSummaryResponse.builder()
                .rollNo(studentSaved.getRollNo())
                .name(studentSaved.getName())
                .programmeName(studentSaved.getProgramme().getProgrammeName())
                .yearOfStudy(studentSaved.getYearOfStudy())
                .accountStatus(studentSaved.getAccountStatus())
                .build();

    }

    public ActivationGenerateResponse generateActivationCode(String rollNo)
    {
        Student student = studentRepository.findById(rollNo)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));

        Optional<StudentActivation> studentActivation =
                activationRepository.findById(rollNo);
        LocalDateTime now = LocalDateTime.now();
        if(studentActivation.isPresent())
        {
            StudentActivation activation = studentActivation.get();

            if(activation.getActivated())
            {
                throw new StudentAlreadyActivatedException("Student account is already activated.");
            }

            if(activation.getExpiresAt().isAfter(now))
            {
                throw new ActivationCodeAlreadyGeneratedException("Activation code already generated");
            }

            if(activation.getExpiresAt().isBefore(now))
            {
                activation.setExpiresAt(now.plusMinutes(10));
                activation.setActivationCode(randomCodeService.generateCode());
                activation.setActivated(false);
                activationRepository.save(activation);
                return mapToActivationResponse(activation);
            }

        }

        StudentActivation createActivation = StudentActivation.builder()
                .activationCode(randomCodeService.generateCode())
                .activated(false)
                .student(student)
                .expiresAt(now.plusMinutes(10))
                .build();

        activationRepository.save(createActivation);

        return mapToActivationResponse(createActivation);

    }


    public String loginActivation( StudentActivationRequest activationRequest)
    {
        Student student = getStudent(activationRequest.getRollNo());

        if(studentRepository.existsByPersonalEmail(activationRequest.getPersonalEmail()))
        {
            throw new EmailAlreadyExistsException("give Email already exists");
        }
        StudentActivation activation = activationRepository.findById(activationRequest.getRollNo())
                .orElseThrow(()-> new ActivationNotFoundException("Activation code not found! Pleas generate code first"));


            if (activation.getActivated()) {
                throw new StudentAlreadyActivatedException("Student Login already activated please try login");
            }

            if(activation.getExpiresAt().isAfter(LocalDateTime.now()))
            {
                if(activation.getActivationCode().equals(activationRequest.getActivationCode()))
                {
                    student.setPassword(passwordEncoder.encode(activationRequest.getPassword()));

                    student.setPersonalEmail(activationRequest.getPersonalEmail());
                    student.setAccountStatus(AccountStatus.PROFILE_PENDING);
                    activation.setActivated(true);
                    studentRepository.save(student);
                    activationRepository.save(activation);
                }
                else
                {
                    throw new ActivationCodeInvalidException("Activation code is Invalid");
                }
            }
            else
            {
                throw new ActivationCodeExpired("Activation code expired generate new");
            }

        return "Your login Activation is successful";
    }
}
