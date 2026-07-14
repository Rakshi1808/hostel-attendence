package com.hostel.management.face.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.common.service.CommonService;
import com.hostel.management.face.FaceEmbedding;
import com.hostel.management.face.FaceEmbeddingRepository;
import com.hostel.management.face.RegisteredBy;
import com.hostel.management.face.client.FaceClient;
import com.hostel.management.face.dto.FaceRegistrationResponse;
import com.hostel.management.face.dto.RegisterFaceResponse;
import com.hostel.management.student.entity.Student;
import com.hostel.management.student.repository.StudentRepository;
import com.hostel.management.student.service.StudentService;
import jakarta.transaction.Transactional;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FaceService
{
    @Value("${face.upload.path}")
    private String uploadPath;

    @Autowired
    private ObjectMapper objectMapper;

    FaceEmbeddingRepository faceEmbeddingRepository;
    CommonService commonService;
    FaceClient faceClient;
    StudentRepository studentRepository;
    public FaceService(CommonService commonService, FaceClient faceClient
    , FaceEmbeddingRepository faceEmbeddingRepository,StudentRepository studentRepository)
    {
        this.commonService = commonService;
        this.faceClient = faceClient;
        this.faceEmbeddingRepository = faceEmbeddingRepository;
        this.studentRepository = studentRepository;
    }
    private String convertEmbedding(List<Float> embedding)
    {
        try{
            return objectMapper.writeValueAsString(embedding);
        }
        catch(JsonProcessingException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
    private String savePhoto(MultipartFile photo, String rollNo) throws IOException {

        Path uploadDirectory = Paths.get(uploadPath);

        Files.createDirectories(uploadDirectory);

        String fileName = rollNo + ".jpg";

        Path filePath = uploadDirectory.resolve(fileName);

        Files.copy(
                photo.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return filePath.toString();
    }
    @Transactional
    public FaceRegistrationResponse registerFace(MultipartFile photo)
    {
        Student student = commonService.getCurrentStudent();

        if (student.getAccountStatus() != AccountStatus.FACE_PENDING) {
            throw new RuntimeException("Face registration is not allowed.");
        }

        if (photo.isEmpty()) {
            throw new RuntimeException("Photo is required.");
        }

        RegisterFaceResponse response = faceClient.registerFace(photo,student.getRollNo());
        if(!response.isSuccess())
        {
            throw new RuntimeException(response.getMessage());
        }
        String photoPath = null;
        String embeddingJason = convertEmbedding(response.getEmbedding());

        try {
             photoPath = savePhoto(photo, student.getRollNo());

        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo");
        }
        FaceEmbedding faceEmbedding= FaceEmbedding.builder()
                .student(student)
                .photoPath(photoPath)
                .registeredBy(RegisteredBy.WARDEN)
                .embedding(embeddingJason)
                .build();
        faceEmbeddingRepository.save(faceEmbedding);
        student.setAccountStatus(AccountStatus.FACE_APPROVAL_PENDING);
        studentRepository.save(student);

        FaceRegistrationResponse faceResponse = FaceRegistrationResponse.builder()
                .message("Face registered sucessfully. Waiting for warden approval.")
                .success(true)
                .build();
        return faceResponse;

    }
}
