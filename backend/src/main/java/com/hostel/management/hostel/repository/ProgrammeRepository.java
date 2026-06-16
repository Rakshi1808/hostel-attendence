package com.hostel.management.hostel.repository;

import com.hostel.management.hostel.entity.Programme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgrammeRepository extends JpaRepository<Programme,Integer>
{
}
