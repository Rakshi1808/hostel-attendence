package com.hostel.management.student.repository;

import com.hostel.management.student.entity.Programme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgrammeRepository extends JpaRepository<Programme,Integer>
{
    boolean existsByProgrammeName(String programmeName);

    Optional<Programme> findByProgrammeName(String name);
}
