package com.hostel.management.student.repository;

import com.hostel.management.student.entity.ParentContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentContactRepository extends JpaRepository<ParentContact, Integer> {
}
