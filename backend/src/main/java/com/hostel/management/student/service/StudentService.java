package com.hostel.management.student.service;

import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.common.service.CommonService;
import com.hostel.management.common.service.RandomCodeService;
import com.hostel.management.hostel.Room;
import com.hostel.management.hostel.RoomRepository;
import com.hostel.management.student.dto.*;
import com.hostel.management.student.entity.ParentContact;
import com.hostel.management.student.entity.Programme;
import com.hostel.management.student.entity.Student;
import com.hostel.management.student.entity.StudentActivation;
import com.hostel.management.student.exception.*;
import com.hostel.management.student.repository.ParentContactRepository;
import com.hostel.management.student.repository.ProgrammeRepository;
import com.hostel.management.student.repository.StudentActivationRepository;
import com.hostel.management.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StudentService
{
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final ProgrammeRepository programmeRepository;
    private final StudentActivationRepository activationRepository;
    private final RandomCodeService randomCodeService;
    private final PasswordEncoder passwordEncoder;
    private final ParentContactRepository parentContactRepository;
    private final CommonService commonService;
    public StudentService(StudentRepository studentRepository,ProgrammeRepository programmeRepository,
                          StudentActivationRepository activationRepository,RandomCodeService randomCodeService
                            ,PasswordEncoder passwordEncoder,ParentContactRepository parentContactRepository,
                          CommonService commonService, RoomRepository roomRepository)
    {
        this.studentRepository = studentRepository;
        this.programmeRepository = programmeRepository;
        this.activationRepository = activationRepository;
        this.randomCodeService = randomCodeService;
        this.passwordEncoder = passwordEncoder;
        this.parentContactRepository = parentContactRepository;
        this.commonService = commonService;
        this.roomRepository = roomRepository;
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

    private StudentProfileResponse mapToProfileResponse(
            Student student)
    {
        ParentContact parentContact = parentContactRepository.findByStudentRollNo(student.getRollNo());
        return StudentProfileResponse.builder()
                .rollNo(student.getRollNo())
                .name(student.getName())
                .personalEmail(student.getPersonalEmail())
                .studentPhone(student.getStudentPhone())
                .programme(student.getProgramme().getProgrammeName())
                .yearOfStudy(student.getYearOfStudy())
                .hostelName(student.getRoom().getHostel().getName())
                .roomNo(student.getRoom().getRoomNo())
                .parentName(parentContact.getParentName())
                .parentPhone(parentContact.getParentPhone())
                .relation(parentContact.getRelation())
                .accountStatus(student.getAccountStatus())
                .build();
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

    @Transactional
    public StudentProfileResponse studentProfileUpdate( StudentProfileRequest studentProfileRequest)
    {

        Student student = commonService.getCurrentStudent();

        if(!student.getAccountStatus().equals(AccountStatus.PROFILE_PENDING))
            throw new RuntimeException("Bad Request");

        Room room = roomRepository
                .findByHostelIdAndRoomNoAndIsActiveTrue(studentProfileRequest.getHostelId(),
                        studentProfileRequest.getRoomNo())
        .orElseThrow(() -> new RuntimeException("Room not found"));

        student.setAccountStatus(AccountStatus.FACE_PENDING);
        student.setRoom(room);
        studentRepository.save(student);

        ParentContact parentContact = ParentContact.builder()
                .parentName(studentProfileRequest.getParentName())
                .parentPhone(studentProfileRequest.getParentPhone())
                .relation(studentProfileRequest.getRelation())
                .student(student)
                .build();
        parentContactRepository.save(parentContact);

        return mapToProfileResponse(student);

    }
}
