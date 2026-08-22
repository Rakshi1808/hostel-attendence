package com.hostel.management.warden;

import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.common.response.ApiResponse;
import com.hostel.management.face.FaceEmbedding;
import com.hostel.management.face.FaceEmbeddingRepository;
import com.hostel.management.student.entity.Student;
import com.hostel.management.student.repository.StudentRepository;
import com.hostel.management.warden.dto.PendingFaceResponse;
import com.hostel.management.common.service.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WardenService {
    private final StudentRepository studentRepository;
    private final CommonService commonService;
    private final FaceEmbeddingRepository faceEmbeddingRepository;

    public WardenService(WardenRepository wardenRepository, StudentRepository studentRepository
    , CommonService commonService, FaceEmbeddingRepository faceEmbeddingRepository)
    {
        this.studentRepository = studentRepository;
        this.commonService = commonService;
        this.faceEmbeddingRepository = faceEmbeddingRepository;
    }
    @Transactional
    public List<PendingFaceResponse> pendingFaceResponse()
    {
        Warden warden = commonService.getCurrentWarden();
        List<Student> students =
                studentRepository.findByRoomHostelIdAndAccountStatus(warden.getHostel().getId(),
                        AccountStatus.FACE_APPROVAL_PENDING);
        List<PendingFaceResponse> pendingFace = new ArrayList<>();
        for(Student student : students)
        {
           Optional<FaceEmbedding> optionalEmbedding = faceEmbeddingRepository.findByStudentRollNoAndIsActiveTrue(student.getRollNo());
            if(optionalEmbedding.isEmpty())
                continue;
            FaceEmbedding embedding = optionalEmbedding.get();
           pendingFace.add(PendingFaceResponse.builder()
                   .rollNo(student.getRollNo())
                   .name(student.getName())
                   .photoPath(embedding.getPhotoPath())
                   .registeredAt(embedding.getRegisteredAt())
                   .build());

        }

        return pendingFace;
    }

    @Transactional
    public ApiResponse approveFace(String rollNo)
    {
        Student student = studentRepository.findById(rollNo)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if(student.getAccountStatus() != AccountStatus.FACE_APPROVAL_PENDING)
            throw new RuntimeException("Student is not waiting for approval");

        if(!faceEmbeddingRepository.existsByStudentRollNoAndIsActiveTrue(rollNo))
            throw new RuntimeException("Active face not found");

        student.setAccountStatus(AccountStatus.ACTIVE);
        studentRepository.save(student);

        return ApiResponse.builder()
                .success(true)
                .message("Face approved successfully.")
                .build();
    }

    @Transactional
    public ApiResponse rejectFace(String rollNo)
    {
        Student student = studentRepository.findById(rollNo)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if(student.getAccountStatus() != AccountStatus.FACE_APPROVAL_PENDING)
            throw new RuntimeException("Student is not waiting for approval");

        FaceEmbedding faceEmbedding = faceEmbeddingRepository
                .findByStudentRollNoAndIsActiveTrue(rollNo)
                .orElseThrow(() -> new RuntimeException("Face not found"));

        faceEmbedding.setIsActive(false);
        faceEmbedding.setDeactivatedAt(LocalDateTime.now());

        faceEmbeddingRepository.save(faceEmbedding);

        student.setAccountStatus(AccountStatus.FACE_PENDING);
        studentRepository.save(student);

        return ApiResponse.builder()
                .success(true)
                .message("Face rejected successfully.")
                .build();
    }
}
