package com.hostel.management.student.repository;

import com.hostel.management.student.entity.ParentContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentContactRepository extends JpaRepository<ParentContact, Integer> {

    ParentContact findByStudentRollNo(String rollNo);
}
