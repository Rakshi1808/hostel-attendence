package com.hostel.management.student.repository;

import com.hostel.management.common.enums.AccountStatus;
import com.hostel.management.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String> {
    public Boolean existsByPersonalEmail(String personalEmail);

    List<Student> findByRoomHostelIdAndAccountStatus(
            Integer hostelId,
            AccountStatus accountStatus
    );
}
