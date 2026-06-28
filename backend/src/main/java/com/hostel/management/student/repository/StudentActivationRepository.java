package com.hostel.management.student.repository;

import com.hostel.management.student.entity.StudentActivation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentActivationRepository extends JpaRepository<StudentActivation,String>
{
}
