package com.hostel.management.attendance.repository;

import com.hostel.management.attendance.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession,Integer> {
}
