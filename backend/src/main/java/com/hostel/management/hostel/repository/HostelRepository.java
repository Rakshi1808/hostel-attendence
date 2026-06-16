package com.hostel.management.hostel.repository;

import com.hostel.management.hostel.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelRepository extends JpaRepository<Hostel,Integer> {
}
