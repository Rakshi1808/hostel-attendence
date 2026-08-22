package com.hostel.management.attendance.service;

import com.hostel.management.attendance.dto.AttendanceStatusResponse;
import com.hostel.management.attendance.dto.LocationVerificationResult;
import com.hostel.management.attendance.dto.MarkAttendanceResponse;
import com.hostel.management.attendance.entity.AttendanceSession;
import com.hostel.management.attendance.location.LocationVerifier;
import com.hostel.management.attendance.repository.AttendanceLogRepository;
import com.hostel.management.attendance.repository.AttendanceSessionRepository;
import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.common.service.CommonService;
import com.hostel.management.face.FaceEmbedding;
import com.hostel.management.face.FaceEmbeddingRepository;
import com.hostel.management.face.client.FaceClient;
import com.hostel.management.face.dto.VerifyFaceResponse;
import com.hostel.management.hostel.Hostel;
import com.hostel.management.student.entity.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class AttendanceService {

    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final CommonService commonService;
    private final LocationVerifier locationVerifier;
    private final FaceClient    faceClient;
    private final FaceEmbeddingRepository faceEmbeddingRepository;
    @Value("${face.verification.threshold}")
    private float faceVerificationThreshold;

    public AttendanceService (AttendanceSessionRepository attendanceSessionRepository,
                              AttendanceLogRepository attendanceLogRepository, CommonService commonService,
                              LocationVerifier locationVerifier, FaceClient faceClient,
                              FaceEmbeddingRepository faceEmbeddingRepository)
    {
        this.attendanceSessionRepository=attendanceSessionRepository;
        this.attendanceLogRepository=attendanceLogRepository;
        this.commonService=commonService;
        this.locationVerifier=locationVerifier;
        this.faceClient=faceClient;
        this.faceEmbeddingRepository=faceEmbeddingRepository;
    }

    public AttendanceStatusResponse checkAttendanceEligibility() {
        Student student = commonService.getCurrentStudent();

        if (student.getAccountStatus() != AccountStatus.ACTIVE) {
            return AttendanceStatusResponse.builder()
                    .eligible(false)
                    .message("Student account is not active")
                    .build();
        }

        Hostel hostel = student.getRoom().getHostel();

        AttendanceSession session =
                getOrCreateTodaySession(hostel);

        LocalTime now = LocalTime.now();

        if (now.isBefore(session.getWindowStart())
                || now.isAfter(session.getWindowEnd())) {

            return AttendanceStatusResponse.builder()
                    .eligible(false)
                    .message("Attendance window is closed")
                    .build();
        }

        if (attendanceLogRepository.existsByStudentRollNoAndSessionId(
                student.getRollNo(),
                session.getId())) {

            return AttendanceStatusResponse.builder()
                    .eligible(false)
                    .message("Attendance already marked")
                    .build();
        }

        return AttendanceStatusResponse.builder()
                .eligible(true)
                .message("Attendance is available")
                .build();
    }

    private AttendanceSession getOrCreateTodaySession(Hostel hostel) {
        LocalDate today = LocalDate.now();

        return attendanceSessionRepository
                .findBySessionDateAndHostelId(today, hostel.getId())
                .orElseGet(() -> {
                    AttendanceSession session = AttendanceSession.builder()
                            .sessionDate(today)
                            .windowStart(LocalTime.of(8, 30))
                            .windowEnd(LocalTime.of(12, 30))
                            .hostel(hostel)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return attendanceSessionRepository.save(session);
                });
    }

    public MarkAttendanceResponse markAttendance(MultipartFile photo, Double latitude, Double longitude) {
        Student student = commonService.getCurrentStudent();
        if (student.getAccountStatus() != AccountStatus.ACTIVE) {
            return MarkAttendanceResponse.builder()
                    .success(false)
                    .message("Student account is not active")
                    .build();
        }

        AttendanceSession session =
                getOrCreateTodaySession(
                        student.getRoom().getHostel()
                );

        LocalTime now = LocalTime.now();

        if (now.isBefore(session.getWindowStart())
                || now.isAfter(session.getWindowEnd())) {

            return MarkAttendanceResponse.builder()
                    .success(false)
                    .message("Attendance window is closed")
                    .build();
        }

        if (attendanceLogRepository.existsByStudentRollNoAndSessionId(
                student.getRollNo(),
                session.getId())) {

            return MarkAttendanceResponse.builder()
                    .success(false)
                    .message("Attendance already marked")
                    .build();
        }

        LocationVerificationResult locationResult =
                locationVerifier.verify(
                        student,
                        latitude,
                        longitude
                );

        if (!locationResult.isValid()) {
            return MarkAttendanceResponse.builder()
                    .success(false)
                    .message(locationResult.getMessage())
                    .build();
        }

        FaceEmbedding embedding =
                faceEmbeddingRepository
                        .findByStudentRollNoAndIsActiveTrue(
                                student.getRollNo()
                        )
                        .orElseThrow(() ->
                                new RuntimeException("Active face not found"));

        VerifyFaceResponse response =
                faceClient.verifyFace(
                        photo,
                        student.getRollNo(),
                        embedding.getEmbedding()
                );

        if (response.getConfidence() < faceVerificationThreshold) {
            return MarkAttendanceResponse.builder()
                    .success(false)
                    .message("Face verification failed")
                    .build();
        }


        return null;
    }
}
