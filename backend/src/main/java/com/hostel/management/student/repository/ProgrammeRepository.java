package com.hostel.management.student.repository;

import com.hostel.management.student.entity.Programme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgrammeRepository extends JpaRepository<Programme,Integer>
{
    boolean existsByProgrammeName(String programmeName);
}
