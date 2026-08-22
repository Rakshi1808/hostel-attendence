package com.hostel.management.attendance.repository;

import com.hostel.management.attendance.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession,Integer> {
    Optional<AttendanceSession> findBySessionDateAndHostelId(
            LocalDate sessionDate,
            Integer hostelId
    );
}
