package com.hostel.management.attendance.repository;

import com.hostel.management.attendance.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceLogRepository extends JpaRepository<AttendanceLog,Integer> {
}
